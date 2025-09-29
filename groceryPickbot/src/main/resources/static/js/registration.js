function registrationHandler() {
    document.getElementById('registrationForm').addEventListener('submit', function(event) {
        event.preventDefault();
        document.querySelectorAll('.error').forEach(e => e.textContent = '');
        document.getElementById('formMessage').textContent = '';

        const form = event.target;
        const formData = new FormData(form);
        const object = {};
        formData.forEach((value, key) => object[key] = value);
        fetch('/api/auth/registration', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(object)

        })
            .then(async response => {
                const data = await response.json();
                if (!response.ok) {
                    Object.keys(data).forEach(key => {
                        const errorDiv = document.getElementById(key + 'Error');
                        if (errorDiv) {
                            errorDiv.textContent = data[key];
                        } else {
                            document.getElementById('formMessage').textContent = data[key];
                        }
                    });
                    throw new Error('Validation failed');
                }
                window.location.href = '/login.html';
            })
            .catch(err => console.error(err));
    });
}

document.addEventListener('DOMContentLoaded', registrationHandler);

