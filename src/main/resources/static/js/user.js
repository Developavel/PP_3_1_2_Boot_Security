/**
 * user.js - Управление страницей пользователя
 * Загружает данные текущего пользователя через REST API
 */

document.addEventListener('DOMContentLoaded', function() {
    loadUserInfo().catch(function(error) {
        console.error('Ошибка загрузки данных пользователя:', error);
        showError('Не удалось загрузить данные пользователя');
    });
});

/**
 * Загружает и отображает информацию о текущем пользователе
 * @returns {Promise<void>}
 */
function loadUserInfo() {
    return API.user.getCurrent()
        .then(function(user) {
            renderUserInfo(user);
        })
        .catch(function(error) {
            console.error('Ошибка получения данных пользователя:', error);
            throw error;
        });
}

/**
 * Рендерит информацию о пользователе в таблицу
 * @param {Object} user - Данные пользователя
 */
function renderUserInfo(user) {
    var tbody = document.getElementById('user-info-body');

    if (!user) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">Данные пользователя не найдены</td></tr>';
        return;
    }

    // Формируем строку с ролями
    var rolesString = '';
    if (user.roles && user.roles.length > 0) {
        var roleNames = [];
        for (var i = 0; i < user.roles.length; i++) {
            roleNames.push(user.roles[i].name.replace('ROLE_', ''));
        }
        rolesString = roleNames.join(', ');
    }

    tbody.innerHTML =
        '<tr>' +
        '<td>' + user.id + '</td>' +
        '<td>' + escapeHtml(user.firstName || '') + '</td>' +
        '<td>' + escapeHtml(user.lastName || '') + '</td>' +
        '<td>' + (user.age || '') + '</td>' +
        '<td>' + escapeHtml(user.email || '') + '</td>' +
        '<td>' + escapeHtml(rolesString) + '</td>' +
        '</tr>';
}

/**
 * Экранирует HTML-сущности для предотвращения XSS
 * @param {string} text - Текст для экранирования
 * @returns {string} - Экранированный текст
 */
function escapeHtml(text) {
    if (!text) return '';
    var div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * Показывает сообщение об ошибке
 * @param {string} message - Текст сообщения
 */
function showError(message) {
    // Ищем или создаем контейнер для сообщений
    var container = document.getElementById('message-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'message-container';
        container.style.position = 'fixed';
        container.style.top = '20px';
        container.style.right = '20px';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
    }

    var alert = document.createElement('div');
    alert.className = 'alert alert-danger alert-dismissible fade show';
    alert.innerHTML = escapeHtml(message) +
        '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>';
    container.appendChild(alert);

    // Автоматическое скрытие через 5 секунд
    setTimeout(function() {
        if (alert.parentNode) {
            alert.remove();
        }
    }, 5000);
}