function loginHandler() {
    document.getElementById('loginForm').addEventListener('submit', function(event) {
        event.preventDefault();
        document.getElementById('loginError').textContent = '';

        const form = event.target;
        const formData = new FormData(form);

        const object = {};
        formData.forEach((value, key) => {
            object[key] = value;
        });

        fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(object)
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(data => {
                        throw new Error(data.error || 'Invalid username or password');
                    });
                }
                window.location.href = '/welcome.html';
            })
            .catch(err => {
                document.getElementById('loginError').textContent = err.message;
            });
    });
}

document.addEventListener('DOMContentLoaded', loginHandler);