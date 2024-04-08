async function authenticateAdmin() {
	const pass = document.getElementById("pass").value;
	const body = new URLSearchParams();
	body.append('login', 'admin');
	body.append('password', pass);

	const login = await fetch('http://localhost:8080/user/login', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded'
		},
		body: body.toString()
	});

	console.log(await login.json(), login.status);
	if (login.status === 200) {
		const authHeader = login.headers.get('Authentication');
		localStorage.setItem('auth', authHeader);
		window.location.href = 'admin.html';
	}
}

// On page load
document.addEventListener('DOMContentLoaded', function () {
	// Event handler sur "pass" quand on return
	const passInput = document.getElementById("pass")
	passInput.addEventListener("keyup", function (event) {
		if (event.key === "Enter") {
			authenticateAdmin();
		}
	});

	// Event handler sur "submit" du formulaire
	const submitButton = document.getElementById("submit")
	submitButton.addEventListener("click", authenticateAdmin)
});

