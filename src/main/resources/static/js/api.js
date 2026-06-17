/**
 * API клиент для взаимодействия с REST эндпоинтами
 * Все методы возвращают Promise
 * Поддерживает CSRF-защиту
 */
'use strict';

const API = {
    baseUrl: '',

    _getCsrfToken: function() {
        const tokenMeta = document.querySelector('meta[name="_csrf"]');
        const headerMeta = document.querySelector('meta[name="_csrf_header"]');
        return {
            token: tokenMeta ? tokenMeta.content : null,
            header: headerMeta ? headerMeta.content : null
        };
    },

    _handleResponse: function(response) {
        if (response.status === 204) {
            return null;
        }

        return response.json().then(function(data) {
            if (!response.ok) {
                const errorMessage = data.message || data.error || 'HTTP error! status: ' + response.status;
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
        const csrf = this._getCsrfToken();

        const headers = {
            'Content-Type': 'application/json'
        };

        if (options.headers) {
            for (const key in options.headers) {
                if (options.headers.hasOwnProperty(key)) {
                    headers[key] = options.headers[key];
                }
            }
        }

        if (csrf.token && csrf.header && options.method && options.method !== 'GET') {
            headers[csrf.header] = csrf.token;
        }

        const config = {
            headers: headers,
            credentials: 'same-origin'
        };

        for (const prop in options) {
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
            const payload = {
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
            const payload = {
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