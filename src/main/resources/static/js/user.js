/**
 * user.js - Управление страницей пользователя
 * Загружает данные текущего пользователя через REST API
 */

document.addEventListener('DOMContentLoaded', async () => {
    try {
        await loadUserInfo();
    } catch (error) {
        console.error('Ошибка загрузки данных пользователя:', error);
        showError('Не удалось загрузить данные пользователя');
    }
});

/**
 * Загружает и отображает информацию о текущем пользователе
 */
async function loadUserInfo() {
    try {
        const user = await API.user.getCurrent();
        renderUserInfo(user);
    } catch (error) {
        console.error('Ошибка получения данных пользователя:', error);
        throw error;
    }
}

/**
 * Рендерит информацию о пользователе в таблицу
 */
function renderUserInfo(user) {
    const tbody = document.getElementById('user-info-body');

    if (!user) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center">Данные пользователя не найдены</td>
            </tr>
        `;
        return;
    }

    // Формируем строку с ролями
    const rolesString = user.roles && user.roles.length > 0
        ? user.roles.map(role => role.name.replace('ROLE_', '')).join(', ')
        : '';

    tbody.innerHTML = `
        <tr>
            <td>${user.id}</td>
            <td>${escapeHtml(user.firstName || '')}</td>
            <td>${escapeHtml(user.lastName || '')}</td>
            <td>${user.age || ''}</td>
            <td>${escapeHtml(user.email || '')}</td>
            <td>${escapeHtml(rolesString)}</td>
        </tr>
    `;
}

/**
 * Экранирует HTML-сущности для предотвращения XSS
 */
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * Показывает сообщение об ошибке
 */
function showError(message) {
    // Ищем или создаем контейнер для сообщений
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
    alert.innerHTML = `
        ${escapeHtml(message)}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    container.appendChild(alert);

    // Автоматическое скрытие через 5 секунд
    setTimeout(() => {
        if (alert.parentNode) {
            alert.remove();
        }
    }, 5000);
}