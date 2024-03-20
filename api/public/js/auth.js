async function authenticateAdmin() {
    const pass = document.getElementById("pass").value
    const body = JSON.stringify({login: "admin", password: pass})
    console.log(body)
	const login = await fetch("http://localhost:8080/user/login", {
        mode: 'cors',
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({login: "admin", password: "admin"})
	})



	console.log(await login.json())
}