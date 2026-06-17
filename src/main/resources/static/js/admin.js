/**
 * admin.js - Управление админской панелью
 * Fetch API для взаимодействия с REST бэкендом
 */
'use strict';

const COLUMN_COUNT = 8;

const Bootstrap = window.bootstrap || {};
const BootstrapTab = Bootstrap.Tab || null;
const BootstrapModal = Bootstrap.Modal || null;

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
    showTableLoading('users-table-body', COLUMN_COUNT);
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
    const tbody = document.getElementById('users-table-body');
    if (!users || users.length === 0) {
        showTableEmpty('users-table-body', COLUMN_COUNT);
        return;
    }

    tbody.innerHTML = '';
    for (let i = 0; i < users.length; i++) {
        const user = users[i];
        const row = '<tr>' +
            '<td>' + user.id + '</td>' +
            '<td>' + escapeHtml(user.firstName) + '</td>' +
            '<td>' + escapeHtml(user.lastName) + '</td>' +
            '<td>' + user.age + '</td>' +
            '<td>' + escapeHtml(user.email) + '</td>' +
            '<td>' + escapeHtml(getUserRolesString(user.roles)) + '</td>' +
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
    const selects = document.querySelectorAll('select[name="roleIds"]');
    let options = '';
    for (let i = 0; i < roles.length; i++) {
        options += '<option value="' + roles[i].id + '">' + roles[i].name.replace('ROLE_', '') + '</option>';
    }

    for (let j = 0; j < selects.length; j++) {
        const select = selects[j];
        const currentValues = [];
        const selectedOptions = select.selectedOptions;
        for (let k = 0; k < selectedOptions.length; k++) {
            currentValues.push(selectedOptions[k].value);
        }
        select.innerHTML = options;
        if (currentValues.length > 0) {
            const optionElements = select.options;
            for (let l = 0; l < optionElements.length; l++) {
                if (currentValues.indexOf(optionElements[l].value) !== -1) {
                    optionElements[l].selected = true;
                }
            }
        }
    }
}

// ==================== МОДАЛЬНЫЕ ОКНА ====================

function initModals() {
    const editModal = document.getElementById('editModal');
    if (editModal) {
        editModal.addEventListener('show.bs.modal', function(event) {
            const button = event.relatedTarget;
            if (button && button.dataset.user) {
                try {
                    const user = JSON.parse(button.dataset.user);
                    fillEditModal(user);
                } catch (error) {
                    console.error('Ошибка парсинга данных пользователя:', error);
                }
            }
        });
        const editForm = document.getElementById('editForm');
        if (editForm) {
            editForm.addEventListener('submit', handleEditSubmit);
        }
    }

    const deleteModal = document.getElementById('deleteModal');
    if (deleteModal) {
        deleteModal.addEventListener('show.bs.modal', function(event) {
            const button = event.relatedTarget;
            if (button && button.dataset.user) {
                try {
                    const user = JSON.parse(button.dataset.user);
                    fillDeleteModal(user);
                } catch (error) {
                    console.error('Ошибка парсинга данных пользователя:', error);
                }
            }
        });
        const deleteForm = document.getElementById('deleteForm');
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

    const roleSelect = document.getElementById('editRoleIds');
    if (roleSelect && user.roles) {
        const userRoleIds = [];
        for (let i = 0; i < user.roles.length; i++) {
            userRoleIds.push(user.roles[i].id.toString());
        }
        const options = roleSelect.options;
        for (let j = 0; j < options.length; j++) {
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
    const createForm = document.getElementById('createUserForm');
    if (createForm) {
        createForm.addEventListener('submit', handleCreateSubmit);
    }
}

function getStringValue(formData, key) {
    const value = formData.get(key);
    if (value === null || value === undefined) {
        return '';
    }
    if (typeof value === 'string') {
        return value;
    }
    if (value instanceof File) {
        return value.name || '';
    }
    return String(value);
}

function showBootstrapTab(tabId) {
    const tabElement = document.getElementById(tabId);
    if (tabElement && BootstrapTab) {
        const tab = new BootstrapTab(tabElement);
        tab.show();
    }
}

function hideBootstrapModal(modalId) {
    const modalElement = document.getElementById(modalId);
    if (modalElement && BootstrapModal) {
        const modal = BootstrapModal.getInstance(modalElement);
        if (modal) {
            modal.hide();
        }
    }
}

function handleCreateSubmit(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);

    const firstName = getStringValue(formData, 'firstName');
    const lastName = getStringValue(formData, 'lastName');
    const ageValue = getStringValue(formData, 'age');
    const email = getStringValue(formData, 'email');
    const password = getStringValue(formData, 'password');
    const roleIdsRaw = formData.getAll('roleIds');

    const age = parseInt(ageValue, 10);
    if (isNaN(age) || age < 1 || age > 150) {
        showError('Введите корректный возраст (от 1 до 150)');
        return;
    }

    const userData = {
        firstName: firstName,
        lastName: lastName,
        age: age,
        email: email,
        password: password,
        roleIds: []
    };

    for (let i = 0; i < roleIdsRaw.length; i++) {
        const id = parseInt(roleIdsRaw[i], 10);
        if (!isNaN(id)) {
            userData.roleIds.push(id);
        }
    }

    if (!userData.firstName || !userData.lastName || !userData.email || !userData.password) {
        showError('Пожалуйста, заполните все обязательные поля');
        return;
    }

    if (userData.roleIds.length === 0) {
        showError('Выберите хотя бы одну роль');
        return;
    }

    const submitBtn = form.querySelector('button[type="submit"]');
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
            const roleSelect = document.getElementById('createRoleIds');
            if (roleSelect) {
                const options = roleSelect.options;
                for (let i = 0; i < options.length; i++) {
                    options[i].selected = false;
                }
            }
            showSuccess('Пользователь успешно создан!');
            showBootstrapTab('users-table-tab');
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

    const form = event.target;
    const formData = new FormData(form);

    const idValue = getStringValue(formData, 'id');
    const id = parseInt(idValue, 10);

    const roleSelect = document.getElementById('editRoleIds');
    const selectedRoles = [];
    if (roleSelect) {
        const selectedOptions = roleSelect.selectedOptions;
        for (let i = 0; i < selectedOptions.length; i++) {
            const val = parseInt(selectedOptions[i].value, 10);
            if (!isNaN(val)) {
                selectedRoles.push(val);
            }
        }
    }

    const firstName = getStringValue(formData, 'firstName');
    const lastName = getStringValue(formData, 'lastName');
    const ageValue = getStringValue(formData, 'age');
    const email = getStringValue(formData, 'email');
    const newPassword = getStringValue(formData, 'newPassword');

    const age = parseInt(ageValue, 10);
    if (isNaN(age) || age < 1 || age > 150) {
        showError('Введите корректный возраст (от 1 до 150)');
        return;
    }

    const userData = {
        firstName: firstName,
        lastName: lastName,
        age: age,
        email: email,
        newPassword: newPassword || undefined,
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

    const submitBtn = form.querySelector('button[type="submit"]');
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.textContent = 'Сохранение...';
    }

    API.users.update(id, userData)
        .then(function() {
            return loadUsers();
        })
        .then(function() {
            hideBootstrapModal('editModal');
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

    const form = event.target;
    const formData = new FormData(form);

    const idValue = getStringValue(formData, 'id');
    const id = parseInt(idValue, 10);

    const submitBtn = form.querySelector('button[type="submit"]');
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.textContent = 'Удаление...';
    }

    API.users.delete(id)
        .then(function() {
            return loadUsers();
        })
        .then(function() {
            hideBootstrapModal('deleteModal');
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