/**
 * admin.js - Управление админской панелью
 * Использует Fetch API для взаимодействия с REST бэкендом
 */

let allUsers = [];
let allRoles = [];

document.addEventListener('DOMContentLoaded', async () => {
    try {
        await Promise.all([loadUsers(), loadRoles()]);
        initEventHandlers();
        initModals();
    } catch (error) {
        console.error('Ошибка при инициализации страницы:', error);
        showError('Не удалось загрузить данные. Пожалуйста, обновите страницу.');
    }
});

// ==================== ЗАГРУЗКА ДАННЫХ ====================

async function loadUsers() {
    try {
        allUsers = await API.users.getAll();
        renderUsersTable(allUsers);
    } catch (error) {
        console.error('Ошибка загрузки пользователей:', error);
        showError('Не удалось загрузить список пользователей');
    }
}

async function loadRoles() {
    try {
        allRoles = await API.roles.getAll();
        renderRoleSelects(allRoles);
    } catch (error) {
        console.error('Ошибка загрузки ролей:', error);
        showError('Не удалось загрузить список ролей');
    }
}

// ==================== РЕНДЕРИНГ ====================

function renderUsersTable(users) {
    const tbody = document.querySelector('#users-table tbody');
    if (!users || users.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8" class="text-center">Пользователи не найдены</td></tr>`;
        return;
    }
    tbody.innerHTML = users.map(user => `
        <tr>
            <td>${user.id}</td>
            <td>${escapeHtml(user.firstName)}</td>
            <td>${escapeHtml(user.lastName)}</td>
            <td>${user.age}</td>
            <td>${escapeHtml(user.email)}</td>
            <td>${getUserRolesString(user.roles)}</td>
            <td>
                <button type="button" class="btn btn-info text-white btn-sm edit-btn"
                        data-bs-toggle="modal" data-bs-target="#editModal"
                        data-user='${escapeJson(JSON.stringify(user))}'>
                    Edit
                </button>
            </td>
            <td>
                <button type="button" class="btn btn-danger btn-sm delete-btn"
                        data-bs-toggle="modal" data-bs-target="#deleteModal"
                        data-user='${escapeJson(JSON.stringify(user))}'>
                    Delete
                </button>
            </td>
        </tr>
    `).join('');
}

function renderRoleSelects(roles) {
    const selects = document.querySelectorAll('select[name="roleIds"]');
    const options = roles.map(role => `
        <option value="${role.id}">${role.name.replace('ROLE_', '')}</option>
    `).join('');
    selects.forEach(select => {
        const currentValues = Array.from(select.selectedOptions).map(opt => opt.value);
        select.innerHTML = options;
        if (currentValues.length > 0) {
            Array.from(select.options).forEach(opt => {
                if (currentValues.includes(opt.value)) opt.selected = true;
            });
        }
    });
}

function getUserRolesString(roles) {
    if (!roles || roles.length === 0) return '';
    return roles.map(role => role.name.replace('ROLE_', '')).join(', ');
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
        const userRoleIds = user.roles.map(function(role) {
            return role.id.toString();
        });
        Array.from(roleSelect.options).forEach(function(option) {
            option.selected = userRoleIds.includes(option.value);
        });
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

async function handleCreateSubmit(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);

    // Получаем значения с проверкой на null
    const firstName = formData.get('firstName');
    const lastName = formData.get('lastName');
    const ageValue = formData.get('age');
    const email = formData.get('email');
    const password = formData.get('password');
    const roleIdsRaw = formData.getAll('roleIds');

    const userData = {
        firstName: typeof firstName === 'string' ? firstName : '',
        lastName: typeof lastName === 'string' ? lastName : '',
        age: typeof ageValue === 'string' ? parseInt(ageValue) : 0,
        email: typeof email === 'string' ? email : '',
        password: typeof password === 'string' ? password : '',
        roleIds: roleIdsRaw.map(function(id) {
            return typeof id === 'string' ? parseInt(id) : 0;
        }).filter(function(id) {
            return !isNaN(id);
        })
    };

    if (!userData.firstName || !userData.lastName || !userData.email || !userData.password) {
        showError('Пожалуйста, заполните все обязательные поля');
        return;
    }

    try {
        const submitBtn = form.querySelector('button[type="submit"]');
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.textContent = 'Создание...';
        }

        await API.users.create(userData);
        await loadUsers();
        form.reset();
        showSuccess('Пользователь успешно создан!');

        const usersTab = document.getElementById('users-table-tab');
        if (usersTab && typeof bootstrap !== 'undefined') {
            const tab = new bootstrap.Tab(usersTab);
            tab.show();
        }
    } catch (error) {
        console.error('Ошибка создания пользователя:', error);
        showError('Ошибка создания пользователя: ' + (error.message || 'Неизвестная ошибка'));
    } finally {
        const submitBtn = form.querySelector('button[type="submit"]');
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Add new user';
        }
    }
}

async function handleEditSubmit(event) {
    event.preventDefault();

    const form = event.target;
    const formData = new FormData(form);

    const idValue = formData.get('id');
    const id = typeof idValue === 'string' ? parseInt(idValue) : 0;

    const roleSelect = document.getElementById('editRoleIds');
    let selectedRoles = [];
    if (roleSelect) {
        selectedRoles = Array.from(roleSelect.selectedOptions).map(function(opt) {
            return parseInt(opt.value);
        }).filter(function(id) {
            return !isNaN(id);
        });
    }

    const firstName = formData.get('firstName');
    const lastName = formData.get('lastName');
    const ageValue = formData.get('age');
    const email = formData.get('email');
    const newPassword = formData.get('newPassword');

    const userData = {
        firstName: typeof firstName === 'string' ? firstName : '',
        lastName: typeof lastName === 'string' ? lastName : '',
        age: typeof ageValue === 'string' ? parseInt(ageValue) : 0,
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

    try {
        const submitBtn = form.querySelector('button[type="submit"]');
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.textContent = 'Сохранение...';
        }

        await API.users.update(id, userData);
        await loadUsers();

        const modalElement = document.getElementById('editModal');
        if (modalElement && typeof bootstrap !== 'undefined') {
            const modal = bootstrap.Modal.getInstance(modalElement);
            if (modal) {
                modal.hide();
            }
        }

        showSuccess('Пользователь успешно обновлен!');
    } catch (error) {
        console.error('Ошибка обновления пользователя:', error);
        showError('Ошибка обновления пользователя: ' + (error.message || 'Неизвестная ошибка'));
    } finally {
        const submitBtn = form.querySelector('button[type="submit"]');
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Edit';
        }
    }
}

async function handleDeleteSubmit(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);

    const idValue = formData.get('id');
    const id = typeof idValue === 'string' ? parseInt(idValue) : 0;

    if (!confirm('Вы уверены, что хотите удалить этого пользователя?')) {
        return;
    }

    try {
        const submitBtn = form.querySelector('button[type="submit"]');
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.textContent = 'Удаление...';
        }

        await API.users.delete(id);
        await loadUsers();

        const modalElement = document.getElementById('deleteModal');
        if (modalElement && typeof bootstrap !== 'undefined') {
            const modal = bootstrap.Modal.getInstance(modalElement);
            if (modal) {
                modal.hide();
            }
        }

        showSuccess('Пользователь успешно удален!');
    } catch (error) {
        console.error('Ошибка удаления пользователя:', error);
        showError('Ошибка удаления пользователя: ' + (error.message || 'Неизвестная ошибка'));
    } finally {
        const submitBtn = form.querySelector('button[type="submit"]');
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Delete';
        }
    }
}

// ==================== ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ====================

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function escapeJson(json) {
    return json.replace(/"/g, '&quot;');
}

function showError(message) {
    let container = document.getElementById('message-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'message-container';
        container.style.position = 'fixed';
        container.style.top = '20px';
        container.style.right = '20px';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
    }
    const alert = document.createElement('div');
    alert.className = 'alert alert-danger alert-dismissible fade show';
    alert.innerHTML = escapeHtml(message) + '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>';
    container.appendChild(alert);
    setTimeout(function() {
        if (alert.parentNode) {
            alert.remove();
        }
    }, 5000);
}

function showSuccess(message) {
    let container = document.getElementById('message-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'message-container';
        container.style.position = 'fixed';
        container.style.top = '20px';
        container.style.right = '20px';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
    }
    const alert = document.createElement('div');
    alert.className = 'alert alert-success alert-dismissible fade show';
    alert.innerHTML = escapeHtml(message) + '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>';
    container.appendChild(alert);
    setTimeout(function() {
        if (alert.parentNode) {
            alert.remove();
        }
    }, 3000);
}