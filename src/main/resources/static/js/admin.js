/**
 * admin.js - Управление админской панелью
 * Использует Fetch API для взаимодействия с REST бэкендом
 */
'use strict';

document.addEventListener('DOMContentLoaded', function() {
    Promise.all([loadUsers(), loadRoles()])
        .then(function() {
            initEventHandlers();
            initModals();
        })
        .catch(function(error) {
            console.error('Ошибка при инициализации страницы:', error);
            showError('Не удалось загрузить данные. Пожалуйста, обновите страницу.');
        });
});

// ==================== ЗАГРУЗКА ДАННЫХ ====================

function loadUsers() {
    showTableLoading('users-table-body', 8);
    return API.users.getAll()
        .then(function(users) {
            renderUsersTable(users);
        })
        .catch(function(error) {
            console.error('Ошибка загрузки пользователей:', error);
            showError('Не удалось загрузить список пользователей');
        });
}

function loadRoles() {
    return API.roles.getAll()
        .then(function(roles) {
            renderRoleSelects(roles);
        })
        .catch(function(error) {
            console.error('Ошибка загрузки ролей:', error);
            showError('Не удалось загрузить список ролей');
        });
}

// ==================== РЕНДЕРИНГ ====================

function renderUsersTable(users) {
    // ✅ Используем getElementById для единообразия
    var tbody = document.getElementById('users-table-body');
    if (!users || users.length === 0) {
        showTableEmpty('users-table-body', 8);
        return;
    }

    tbody.innerHTML = '';
    for (var i = 0; i < users.length; i++) {
        var user = users[i];
        var row = '<tr>' +
            '<td>' + user.id + '</td>' +
            '<td>' + escapeHtml(user.firstName) + '</td>' +
            '<td>' + escapeHtml(user.lastName) + '</td>' +
            '<td>' + user.age + '</td>' +
            '<td>' + escapeHtml(user.email) + '</td>' +
            '<td>' + escapeHtml(getUserRolesString(user.roles)) + '</td>' +  // ✅ Добавлен escapeHtml для ролей
            '<td><button type="button" class="btn btn-info text-white btn-sm edit-btn" ' +
            'data-bs-toggle="modal" data-bs-target="#editModal" ' +
            'data-user=\'' + escapeJson(JSON.stringify(user)) + '\'>Редактировать</button></td>' +
            '<td><button type="button" class="btn btn-danger btn-sm delete-btn" ' +
            'data-bs-toggle="modal" data-bs-target="#deleteModal" ' +
            'data-user=\'' + escapeJson(JSON.stringify(user)) + '\'>Удалить</button></td>' +
            '</tr>';
        tbody.insertAdjacentHTML('beforeend', row);
    }
}

function renderRoleSelects(roles) {
    var selects = document.querySelectorAll('select[name="roleIds"]');
    var options = '';
    for (var i = 0; i < roles.length; i++) {
        options += '<option value="' + roles[i].id + '">' + roles[i].name.replace('ROLE_', '') + '</option>';
    }

    for (var j = 0; j < selects.length; j++) {
        var select = selects[j];
        var currentValues = [];
        var selectedOptions = select.selectedOptions;
        for (var k = 0; k < selectedOptions.length; k++) {
            currentValues.push(selectedOptions[k].value);
        }
        select.innerHTML = options;
        if (currentValues.length > 0) {
            var optionElements = select.options;
            for (var l = 0; l < optionElements.length; l++) {
                if (currentValues.indexOf(optionElements[l].value) !== -1) {
                    optionElements[l].selected = true;
                }
            }
        }
    }
}

// ==================== МОДАЛЬНЫЕ ОКНА ====================

function initModals() {
    var editModal = document.getElementById('editModal');
    if (editModal) {
        editModal.addEventListener('show.bs.modal', function(event) {
            var button = event.relatedTarget;
            if (button && button.dataset.user) {
                try {
                    var user = JSON.parse(button.dataset.user);
                    fillEditModal(user);
                } catch (error) {
                    console.error('Ошибка парсинга данных пользователя:', error);
                }
            }
        });
        var editForm = document.getElementById('editForm');
        if (editForm) {
            editForm.addEventListener('submit', handleEditSubmit);
        }
    }

    var deleteModal = document.getElementById('deleteModal');
    if (deleteModal) {
        deleteModal.addEventListener('show.bs.modal', function(event) {
            var button = event.relatedTarget;
            if (button && button.dataset.user) {
                try {
                    var user = JSON.parse(button.dataset.user);
                    fillDeleteModal(user);
                } catch (error) {
                    console.error('Ошибка парсинга данных пользователя:', error);
                }
            }
        });
        var deleteForm = document.getElementById('deleteForm');
        if (deleteForm) {
            deleteForm.addEventListener('submit', handleDeleteSubmit);
        }
    }
}

function fillEditModal(user) {
    document.getElementById('editUserId').value = user.id;
    document.getElementById('editUserIdDisplay').value = user.id;
    document.getElementById('editFirstName').value = user.firstName || '';
    document.getElementById('editLastName').value = user.lastName || '';
    document.getElementById('editAge').value = user.age || '';
    document.getElementById('editEmail').value = user.email || '';
    document.getElementById('editPassword').value = '';

    var roleSelect = document.getElementById('editRoleIds');
    if (roleSelect && user.roles) {
        var userRoleIds = [];
        for (var i = 0; i < user.roles.length; i++) {
            userRoleIds.push(user.roles[i].id.toString());
        }
        var options = roleSelect.options;
        for (var j = 0; j < options.length; j++) {
            options[j].selected = userRoleIds.indexOf(options[j].value) !== -1;
        }
    }
}

function fillDeleteModal(user) {
    document.getElementById('deleteUserId').value = user.id;
    document.getElementById('deleteUserFirstName').value = user.firstName || '';
    document.getElementById('deleteUserLastName').value = user.lastName || '';
    document.getElementById('deleteUserAge').value = user.age || '';
    document.getElementById('deleteUserEmail').value = user.email || '';
    document.getElementById('deleteUserRoles').value = getUserRolesString(user.roles);
    document.getElementById('deleteFormUserId').value = user.id;
}

// ==================== ОБРАБОТЧИКИ СОБЫТИЙ ====================

function initEventHandlers() {
    var createForm = document.getElementById('createUserForm');
    if (createForm) {
        createForm.addEventListener('submit', handleCreateSubmit);
    }
}

function handleCreateSubmit(event) {
    event.preventDefault();
    var form = event.target;
    var formData = new FormData(form);

    var firstName = formData.get('firstName');
    var lastName = formData.get('lastName');
    var ageValue = formData.get('age');
    var email = formData.get('email');
    var password = formData.get('password');
    var roleIdsRaw = formData.getAll('roleIds');

    // ✅ Проверка возраста
    var age = typeof ageValue === 'string' ? parseInt(ageValue) : 0;
    if (isNaN(age) || age < 1 || age > 150) {
        showError('Введите корректный возраст (от 1 до 150)');
        return;
    }

    var userData = {
        firstName: typeof firstName === 'string' ? firstName : '',
        lastName: typeof lastName === 'string' ? lastName : '',
        age: age,
        email: typeof email === 'string' ? email : '',
        password: typeof password === 'string' ? password : '',
        roleIds: []
    };

    for (var i = 0; i < roleIdsRaw.length; i++) {
        var id = parseInt(roleIdsRaw[i]);
        if (!isNaN(id)) {
            userData.roleIds.push(id);
        }
    }

    if (!userData.firstName || !userData.lastName || !userData.email || !userData.password) {
        showError('Пожалуйста, заполните все обязательные поля');
        return;
    }

    // ✅ Добавлена проверка ролей при создании
    if (userData.roleIds.length === 0) {
        showError('Выберите хотя бы одну роль');
        return;
    }

    var submitBtn = form.querySelector('button[type="submit"]');
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.textContent = 'Создание...';
    }

    API.users.create(userData)
        .then(function() {
            return loadUsers();
        })
        .then(function() {
            form.reset();
            showSuccess('Пользователь успешно создан!');

            var usersTab = document.getElementById('users-table-tab');
            if (usersTab && typeof bootstrap !== 'undefined') {
                var tab = new bootstrap.Tab(usersTab);
                tab.show();
            }
        })
        .catch(function(error) {
            console.error('Ошибка создания пользователя:', error);
            showError('Ошибка создания пользователя: ' + (error.message || 'Неизвестная ошибка'));
        })
        .finally(function() {
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.textContent = 'Добавить пользователя';
            }
        });
}

function handleEditSubmit(event) {
    event.preventDefault();

    var form = event.target;
    var formData = new FormData(form);

    var idValue = formData.get('id');
    var id = typeof idValue === 'string' ? parseInt(idValue) : 0;

    var roleSelect = document.getElementById('editRoleIds');
    var selectedRoles = [];
    if (roleSelect) {
        var selectedOptions = roleSelect.selectedOptions;
        for (var i = 0; i < selectedOptions.length; i++) {
            var val = parseInt(selectedOptions[i].value);
            if (!isNaN(val)) {
                selectedRoles.push(val);
            }
        }
    }

    var firstName = formData.get('firstName');
    var lastName = formData.get('lastName');
    var ageValue = formData.get('age');
    var email = formData.get('email');
    var newPassword = formData.get('newPassword');

    // ✅ Проверка возраста
    var age = typeof ageValue === 'string' ? parseInt(ageValue) : 0;
    if (isNaN(age) || age < 1 || age > 150) {
        showError('Введите корректный возраст (от 1 до 150)');
        return;
    }

    var userData = {
        firstName: typeof firstName === 'string' ? firstName : '',
        lastName: typeof lastName === 'string' ? lastName : '',
        age: age,
        email: typeof email === 'string' ? email : '',
        newPassword: typeof newPassword === 'string' && newPassword ? newPassword : undefined,
        roleIds: selectedRoles
    };

    if (!userData.firstName || !userData.lastName || !userData.email) {
        showError('Пожалуйста, заполните все обязательные поля');
        return;
    }

    if (!userData.roleIds || userData.roleIds.length === 0) {
        showError('Пользователь должен иметь хотя бы одну роль');
        return;
    }

    var submitBtn = form.querySelector('button[type="submit"]');
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.textContent = 'Сохранение...';
    }

    API.users.update(id, userData)
        .then(function() {
            return loadUsers();
        })
        .then(function() {
            var modalElement = document.getElementById('editModal');
            if (modalElement && typeof bootstrap !== 'undefined') {
                var modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) {
                    modal.hide();
                }
            }
            showSuccess('Пользователь успешно обновлен!');
        })
        .catch(function(error) {
            console.error('Ошибка обновления пользователя:', error);
            showError('Ошибка обновления пользователя: ' + (error.message || 'Неизвестная ошибка'));
        })
        .finally(function() {
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.textContent = 'Сохранить изменения';
            }
        });
}

function handleDeleteSubmit(event) {
    event.preventDefault();

    var form = event.target;
    var formData = new FormData(form);

    var idValue = formData.get('id');
    var id = typeof idValue === 'string' ? parseInt(idValue) : 0;

    var submitBtn = form.querySelector('button[type="submit"]');
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.textContent = 'Удаление...';
    }

    API.users.delete(id)
        .then(function() {
            return loadUsers();
        })
        .then(function() {
            var modalElement = document.getElementById('deleteModal');
            if (modalElement && typeof bootstrap !== 'undefined') {
                var modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) {
                    modal.hide();
                }
            }
            showSuccess('Пользователь успешно удален!');
        })
        .catch(function(error) {
            console.error('Ошибка удаления пользователя:', error);
            showError('Ошибка удаления пользователя: ' + (error.message || 'Неизвестная ошибка'));
        })
        .finally(function() {
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.textContent = 'Удалить';
            }
        });
}