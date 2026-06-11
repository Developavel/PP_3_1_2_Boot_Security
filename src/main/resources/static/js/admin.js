// Общая функция для заполнения модального окна редактирования
function fillEditModal(button) {
    const fields = ['id', 'firstName', 'lastName', 'age', 'email'];
    fields.forEach(field => {
        const element = document.getElementById(`edit${field.charAt(0).toUpperCase() + field.slice(1)}`);
        if (element) element.value = button.getAttribute(`data-user-${field}`);
    });

    const roleSelect = document.getElementById('editRoleIds');
    const selectedRoleIds = button.getAttribute('data-user-roles-ids')?.split(',') || [];

    for (let i = 0; i < roleSelect.options.length; i++) {
        roleSelect.options[i].selected = selectedRoleIds.includes(roleSelect.options[i].value);
    }
}

// Общая функция для заполнения модального окна удаления
function fillDeleteModal(button) {
    const fields = ['id', 'firstName', 'lastName', 'age', 'email'];
    fields.forEach(field => {
        const element = document.getElementById(`deleteUser${field.charAt(0).toUpperCase() + field.slice(1)}`);
        if (element) element.value = button.getAttribute(`data-user-${field}`);
    });

    // Убираем префикс ROLE_ и устанавливаем значение напрямую
    document.getElementById('deleteUserRoles').value = button.getAttribute('data-user-roles')?.replace(/ROLE_/g, '') || '';
    document.getElementById('deleteFormUserId').value = button.getAttribute('data-user-id');
}

// Инициализация модальных окон
document.addEventListener('DOMContentLoaded', function() {
    const editModal = document.getElementById('editModal');
    if (editModal) {
        editModal.addEventListener('show.bs.modal', function(event) {
            fillEditModal(event.relatedTarget);
        });
    }

    const deleteModal = document.getElementById('deleteModal');
    if (deleteModal) {
        deleteModal.addEventListener('show.bs.modal', function(event) {
            fillDeleteModal(event.relatedTarget);
        });
    }
});