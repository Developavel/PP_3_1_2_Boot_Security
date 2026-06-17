/**
 * utils.js - Общие утилиты
 * Содержит переиспользуемые функции для всего приложения
 */
'use strict';

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
 * Экранирует кавычки в JSON для использования в data-атрибутах
 * @param {string} json - JSON строка
 * @returns {string} - Экранированная строка
 */
function escapeJson(json) {
    if (!json) return '';
    return json
        .replace(/&/g, '&amp;')
        .replace(/'/g, '&#39;')
        .replace(/"/g, '&quot;');
}

/**
 * Показывает сообщение (успех или ошибка)
 * @param {string} message - Текст сообщения
 * @param {string} type - Тип сообщения: 'success' или 'danger'
 * @param {number} timeout - Время до скрытия (мс)
 */
function showMessage(message, type, timeout) {
    type = type || 'danger';
    timeout = timeout || (type === 'success' ? 3000 : 5000);

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
    alert.className = 'alert alert-' + type + ' alert-dismissible fade show';
    alert.innerHTML = escapeHtml(message) +
        '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>';
    container.appendChild(alert);

    setTimeout(function() {
        if (alert.parentNode) {
            alert.remove();
        }
    }, timeout);
}

/**
 * Показывает сообщение об ошибке
 * @param {string} message - Текст сообщения
 */
function showError(message) {
    showMessage(message, 'danger', 5000);
}

/**
 * Показывает сообщение об успехе
 * @param {string} message - Текст сообщения
 */
function showSuccess(message) {
    showMessage(message, 'success', 3000);
}

/**
 * Форматирует роли пользователя в строку
 * @param {Array} roles - Массив ролей
 * @returns {string} - Строка с ролями через запятую
 */
function getUserRolesString(roles) {
    if (!roles || roles.length === 0) return '';
    var roleNames = [];
    for (var i = 0; i < roles.length; i++) {
        roleNames.push(roles[i].name.replace('ROLE_', ''));
    }
    return roleNames.join(', ');
}

/**
 * Показывает индикатор загрузки в таблице
 * @param {string} tableBodyId - ID тела таблицы
 * @param {number} colspan - Количество колонок
 */
function showTableLoading(tableBodyId, colspan) {
    colspan = colspan || 8;
    var tbody = document.getElementById(tableBodyId);
    if (tbody) {
        tbody.innerHTML = '<tr><td colspan="' + colspan + '" class="text-center">' +
            '<div class="spinner-border text-primary spinner-border-sm me-2" role="status">' +
            '<span class="visually-hidden">Loading...</span></div>Загрузка...</td></tr>';
    }
}

/**
 * Показывает сообщение "Нет данных" в таблице
 * @param {string} tableBodyId - ID тела таблицы
 * @param {number} colspan - Количество колонок
 */
function showTableEmpty(tableBodyId, colspan) {
    colspan = colspan || 8;
    var tbody = document.getElementById(tableBodyId);
    if (tbody) {
        tbody.innerHTML = '<tr><td colspan="' + colspan + '" class="text-center">Пользователи не найдены</td></tr>';
    }
}