// Validates the IBAN typed into the form by calling the JSON service at
// /iban/validate/{iban} and writing the outcome under the form. Plain
// JavaScript: the page used to load jQuery 1.7 for this.
document.addEventListener('DOMContentLoaded', function () {
    var form = document.getElementById('ibanForm');
    var input = document.getElementById('ibanRequest');
    var results = document.getElementById('ibanValidationResults');

    function show(message) {
        results.textContent = message;
    }

    form.addEventListener('submit', function (event) {
        event.preventDefault();
        var iban = input.value.trim();
        if (iban === '') {
            show('Enter an IBAN to validate.');
            return;
        }
        fetch(form.dataset.validateUrl + encodeURIComponent(iban), {
            headers: { Accept: 'application/json' }
        })
            .then(function (response) {
                if (!response.ok) {
                    throw new Error('HTTP ' + response.status);
                }
                return response.json();
            })
            .then(function (result) {
                var message = 'The IBAN ' + result.iban + ' is' + (result.valid ? '' : ' not') + ' valid.';
                var reasons = (result.annotations || []).map(function (annotation) {
                    return annotation.message;
                });
                show(reasons.length > 0 ? message + ' ' + reasons.join(' ') : message);
            })
            .catch(function (error) {
                console.log('Failed! ' + error);
                show('An error occurred whilst validating.');
            });
    });
});
