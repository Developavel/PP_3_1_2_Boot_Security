/**
 * API клиент для взаимодействия с REST эндпоинтами
 * Все методы возвращают Promise
 * Поддерживает CSRF-защиту
 */

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

    _handleResponse: async function(response) {
        if (!response.ok) {
            let errorMessage;
            try {
                const errorData = await response.json();
                errorMessage = errorData.message || errorData.error || 'HTTP error! status: ' + response.status;
            } catch (_) {
                const text = await response.text();
                errorMessage = text || 'HTTP error! status: ' + response.status;
            }
            throw new Error(errorMessage);
        }
        if (response.status === 204) {
            return null;
        }
        return response.json();
    },

    _request: async function(endpoint, options) {
        options = options || {};
        const csrf = this._getCsrfToken();

        const headers = {
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

        const config = {
            headers: headers,
            credentials: 'same-origin'
        };

        for (var prop in options) {
            if (options.hasOwnProperty(prop) && prop !== 'headers') {
                config[prop] = options[prop];
            }
        }

        const response = await fetch(this.baseUrl + endpoint, config);
        return this._handleResponse(response);
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

// Поддержка модулей (если используется)
if (typeof module !== 'undefined' && module.exports) {
    module.exports = API;
}