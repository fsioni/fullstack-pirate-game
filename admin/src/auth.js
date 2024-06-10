function init(apiPath, map) {
    const loginDiv = document.getElementById('loginDiv');
    loginDiv.classList.remove('hidden');
    const adminDiv = document.getElementById('adminDiv');

    async function authenticateAdmin() {
        const pass = document.getElementById('pass').value;
        const body = new URLSearchParams();
        body.append('login', 'adm');
        body.append('password', pass);

        const login = await fetch(apiPath + '/user/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: body.toString(),
        });

        if (login.status === 200) {
            const authHeader = login.headers.get('Authentication');
            localStorage.setItem('auth', authHeader);
            const loginDiv = document.getElementById('loginDiv');
            loginDiv.classList.add('hidden');
            adminDiv.classList.remove('hidden');
            map.invalidateSize();
        }
    }

    // On page load
    document.addEventListener('DOMContentLoaded', function () {
        // Event handler sur "pass" quand on return
        const passInput = document.getElementById('pass');
        passInput.addEventListener('keyup', function (event) {
            if (event.key === 'Enter') {
                authenticateAdmin();
            }
        });

        // Event handler sur "submit" du formulaire
        const submitButton = document.getElementById('submit');
        submitButton.addEventListener('click', authenticateAdmin);
    });
}

export default init;
