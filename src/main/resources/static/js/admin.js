// Общие поля пользователя для модальных окон
const USER_FIELDS = [
    'firstName',
    'lastName',
    'age',
    'email'
];

// Установка значения элемента по id
function setValue(id, value) {
    const element = document.getElementById(id);

    if (element) {
        element.value = value ?? '';
    }
}

// Заполнение пользовательских полей формы
function fillFields(prefix, button) {

    USER_FIELDS.forEach(field => {

        const fieldName =
            field.charAt(0).toUpperCase() +
            field.slice(1);

        setValue(
            `${prefix}${fieldName}`,
            button.dataset[`user${fieldName}`]
        );
    });
}

// Инициализация модального окна редактирования
function fillEditModal(button) {

    setValue(
        'editUserId',
        button.dataset.userId
    );

    setValue(
        'editUserIdDisplay',
        button.dataset.userId
    );

    fillFields('edit', button);

    const roleSelect =
        document.getElementById('editRoleIds');

    const selectedRoleIds =
        button.dataset.userRolesIds?.split(',') ?? [];

    [...roleSelect.options].forEach(option => {
        option.selected =
            selectedRoleIds.includes(option.value);
    });
}

// Инициализация модального окна удаления
function fillDeleteModal(button) {

    fillFields('deleteUser', button);

    setValue(
        'deleteUserId',
        button.dataset.userId
    );

    setValue(
        'deleteUserRoles',
        button.dataset.userRoles
            ?.replaceAll('ROLE_', '')
    );

    setValue(
        'deleteFormUserId',
        button.dataset.userId
    );
}

// Подключение обработчиков Bootstrap-модалок
document.addEventListener('DOMContentLoaded', () => {

    document
        .getElementById('editModal')
        ?.addEventListener(
            'show.bs.modal',
            event => fillEditModal(event.relatedTarget)
        );

    document
        .getElementById('deleteModal')
        ?.addEventListener(
            'show.bs.modal',
            event => fillDeleteModal(event.relatedTarget)
        );
});