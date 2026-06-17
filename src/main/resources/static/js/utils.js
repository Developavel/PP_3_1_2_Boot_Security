/**
 * utils.js - Общие утилиты
 * Содержит переиспользуемые функции для всего приложения
 */
'use strict';

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function escapeJson(json) {
    if (!json) return '';
    return json
        .replace(/&/g, '&amp;')
        .replace(/'/g, '&#39;')
        .replace(/"/g, '&quot;');
}

function showMessage(message, type, timeout) {
    type = type || 'danger';
    timeout = timeout || (type === 'success' ? 3000 : 5000);

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

function showError(message) {
    showMessage(message, 'danger', 5000);
}

function showSuccess(message) {
    showMessage(message, 'success', 3000);
}

function getUserRolesString(roles) {
    if (!roles || roles.length === 0) return '';
    const roleNames = [];
    for (let i = 0; i < roles.length; i++) {
        roleNames.push(roles[i].name.replace('ROLE_', ''));
    }
    return roleNames.join(', ');
}

function showTableLoading(tableBodyId, colspan) {
    colspan = colspan || 8;
    const tbody = document.getElementById(tableBodyId);
    if (tbody) {
        tbody.innerHTML = '<tr><td colspan="' + colspan + '" class="text-center">' +
            '<div class="spinner-border text-primary spinner-border-sm me-2" role="status">' +
            '<span class="visually-hidden">Loading...</span></div>Загрузка...</td></tr>';
    }
}

function showTableEmpty(tableBodyId, colspan) {
    colspan = colspan || 8;
    const tbody = document.getElementById(tableBodyId);
    if (tbody) {
        tbody.innerHTML = '<tr><td colspan="' + colspan + '" class="text-center">Пользователи не найдены</td></tr>';
    }
}