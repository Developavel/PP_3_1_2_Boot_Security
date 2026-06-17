/**
 * API клиент для взаимодействия с REST эндпоинтами
 * Все методы возвращают Promise
 * Поддерживает CSRF-защиту
 */
'use strict';

var API = {
    baseUrl: '',

    _getCsrfToken: function() {
        var tokenMeta = document.querySelector('meta[name="_csrf"]');
        var headerMeta = document.querySelector('meta[name="_csrf_header"]');
        return {
            token: tokenMeta ? tokenMeta.content : null,
            header: headerMeta ? headerMeta.content : null
        };
    },

    _handleResponse: function(response) {
        // ✅ Исправлено: сначала проверяем 204 No Content
        if (response.status === 204) {
            return null;
        }

        return response.json().then(function(data) {
            if (!response.ok) {
                var errorMessage = data.message || data.error || 'HTTP error! status: ' + response.status;
                throw new Error(errorMessage);
            }
            return data;
        }).catch(function(error) {
            if (error instanceof SyntaxError) {
                throw new Error('Ошибка парсинга ответа сервера');
            }
            throw error;
        });
    },

    _request: function(endpoint, options) {
        options = options || {};
        var csrf = this._getCsrfToken();

        var headers = {
            'Content-Type': 'application/json'
        };

        if (options.headers) {
            for (var key in options.headers) {
                if (options.headers.hasOwnProperty(key)) {
                    headers[key] = options.headers[key];
                }
            }
        }

        if (csrf.token && csrf.header && options.method && options.method !== 'GET') {
            headers[csrf.header] = csrf.token;
        }

        var config = {
            headers: headers,
            credentials: 'same-origin'
        };

        for (var prop in options) {
            if (options.hasOwnProperty(prop) && prop !== 'headers') {
                config[prop] = options[prop];
            }
        }

        return fetch(this.baseUrl + endpoint, config)
            .then(function(response) {
                return API._handleResponse(response);
            });
    },

    users: {
        getAll: function() {
            return API._request('/api/admin/users');
        },

        getById: function(id) {
            return API._request('/api/admin/users/' + id);
        },

        create: function(userData) {
            var payload = {
                firstName: userData.firstName,
                lastName: userData.lastName,
                age: userData.age,
                email: userData.email,
                password: userData.password,
                roleIds: userData.roleIds || []
            };

            return API._request('/api/admin/users', {
                method: 'POST',
                body: JSON.stringify(payload)
            });
        },

        update: function(id, userData) {
            var payload = {
                firstName: userData.firstName,
                lastName: userData.lastName,
                age: userData.age,
                email: userData.email,
                roleIds: userData.roleIds || []
            };

            if (userData.newPassword) {
                payload.password = userData.newPassword;
            }

            return API._request('/api/admin/users/' + id, {
                method: 'PUT',
                body: JSON.stringify(payload)
            });
        },

        delete: function(id) {
            return API._request('/api/admin/users/' + id, {
                method: 'DELETE'
            });
        }
    },

    user: {
        getCurrent: function() {
            return API._request('/api/user');
        }
    },

    roles: {
        getAll: function() {
            return API._request('/api/admin/roles');
        }
    }
};

if (typeof module !== 'undefined' && module.exports) {
    module.exports = API;
}