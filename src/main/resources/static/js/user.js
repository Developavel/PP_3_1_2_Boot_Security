/**
 * user.js - Управление страницей пользователя
 * Загружает данные текущего пользователя через REST API
 */
'use strict';

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

    var rolesString = getUserRolesString(user.roles);

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