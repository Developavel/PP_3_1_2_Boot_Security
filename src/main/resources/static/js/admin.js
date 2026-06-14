const USER_FIELDS = [
    'firstName',
    'lastName',
    'age',
    'email'
];

function setValue(id, value) {
    const element = document.getElementById(id);

    if (element) {
        element.value = value ?? '';
    }
}

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