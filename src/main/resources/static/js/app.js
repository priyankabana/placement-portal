/*
 * Small bits of page behaviour. Everything important happens on the server,
 * this file only makes the screens a little easier to use.
 */
document.addEventListener('DOMContentLoaded', function () {

    // Ask once before any delete form is submitted
    document.querySelectorAll('form[data-confirm]').forEach(function (form) {
        form.addEventListener('submit', function (event) {
            if (!window.confirm(form.getAttribute('data-confirm'))) {
                event.preventDefault();
            }
        });
    });

    // Dropdown filters submit their own form, so no extra button is needed
    document.querySelectorAll('select[data-auto-submit]').forEach(function (select) {
        select.addEventListener('change', function () {
            select.form.submit();
        });
    });

    // Success messages fade away after a few seconds
    var flash = document.querySelector('.alert-success');
    if (flash) {
        window.setTimeout(function () {
            flash.style.transition = 'opacity .4s';
            flash.style.opacity = '0';
            window.setTimeout(function () { flash.remove(); }, 400);
        }, 4500);
    }
});
