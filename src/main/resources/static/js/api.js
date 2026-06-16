/**
 * API клиент для взаимодействия с REST эндпоинтами
 * Все методы возвращают Promise
 * Поддерживает CSRF-защиту
 */

const API = {
    baseUrl: '',

    _getCsrfToken() {
        const token = document.querySelector('meta[name="_csrf"]')?.content;
        const header = document.querySelector('meta[name="_csrf_header"]')?.content;
        return { token, header };
    },

    async _handleResponse(response) {
        if (!response.ok) {
            let errorMessage;
            try {
                const errorData = await response.json();
                errorMessage = errorData.message || errorData.error || `HTTP error! status: ${response.status}`;
            } catch {
                errorMessage = await response.text() || `HTTP error! status: ${response.status}`;
            }
            throw new Error(errorMessage);
        }
        if (response.status === 204) {
            return null;
        }
        return response.json();
    },

    async _request(endpoint, options = {}) {
        const { token, header } = this._getCsrfToken();

        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };

        if (token && header && options.method && options.method !== 'GET') {
            headers[header] = token;
        }

        const config = {
            headers,
            credentials: 'same-origin',
            ...options
        };

        const response = await fetch(`${this.baseUrl}${endpoint}`, config);
        return this._handleResponse(response);
    },

    users: {
        getAll: () => API._request('/api/admin/users'),

        getById: (id) => API._request(`/api/admin/users/${id}`),

        create: (userData) => {
            const payload = {
                firstName: userData.firstName,
                lastName: userData.lastName,
                age: userData.age,
                email: userData.email,
                password: userData.password,
                roleIds: userData.roleIds || []  // ← ДОБАВЛЕНО
            };

            return API._request('/api/admin/users', {
                method: 'POST',
                body: JSON.stringify(payload)
            });
        },

        update: (id, userData) => {
            const payload = {
                firstName: userData.firstName,
                lastName: userData.lastName,
                age: userData.age,
                email: userData.email,
                roleIds: userData.roleIds || []  // ← ДОБАВЛЕНО! ЭТО БЫЛО ПРОБЛЕМОЙ!
            };

            if (userData.newPassword) {
                payload.password = userData.newPassword;
            }

            return API._request(`/api/admin/users/${id}`, {
                method: 'PUT',
                body: JSON.stringify(payload)
            });
        },

        delete: (id) => API._request(`/api/admin/users/${id}`, {
            method: 'DELETE'
        })
    },

    user: {
        getCurrent: () => API._request('/api/user')
    },

    roles: {
        getAll: () => API._request('/api/admin/roles')
    }
};

if (typeof module !== 'undefined' && module.exports) {
    module.exports = API;
}