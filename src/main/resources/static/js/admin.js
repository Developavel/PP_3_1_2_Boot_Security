const USER_FIELDS = [
    'id',
    'firstName',
    'lastName',
    'age',
    'email'
];

function fillFields(prefix, button) {

    USER_FIELDS.forEach(field => {

        const id =
            prefix +
            field.charAt(0).toUpperCase() +
            field.slice(1);

        const element = document.getElementById(id);

        if (element) {
            element.value =
                button.dataset[
                'user' +
                field.charAt(0).toUpperCase() +
                field.slice(1)
                    ];
        }
    });
}

function fillEditModal(button) {

    fillFields('edit', button);

    const roleSelect =
        document.getElementById('editRoleIds');

    const selectedRoleIds =
        button.dataset.userRolesIds?.split(',') || [];

    [...roleSelect.options].forEach(option => {
        option.selected =
            selectedRoleIds.includes(option.value);
    });
}

function fillDeleteModal(button) {

    fillFields('deleteUser', button);

    document.getElementById('deleteUserRoles').value =
        button.dataset.userRoles
            ?.replace(/ROLE_/g, '') || '';

    document.getElementById('deleteFormUserId').value =
        button.dataset.userId;
}

document.addEventListener('DOMContentLoaded', () => {

    document.getElementById('editModal')
        ?.addEventListener('show.bs.modal',
            e => fillEditModal(e.relatedTarget));

    document.getElementById('deleteModal')
        ?.addEventListener('show.bs.modal',
            e => fillDeleteModal(e.relatedTarget));
});