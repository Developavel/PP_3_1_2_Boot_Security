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

function renderUserInfo(user) {
    const tbody = document.getElementById('user-info-body');

    if (!user) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">Данные пользователя не найдены</td></tr>';
        return;
    }

    const rolesString = getUserRolesString(user.roles);

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