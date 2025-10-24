const API = "http://localhost:8080/api/rsa";

async function getKeys() {
  const res = await fetch(`${API}/keys`);
  const data = await res.json();
  document.getElementById("keys").textContent = 
    `Public Key:\n${data[0]}\n\nPrivate Key:\n${data[1]}`;
}

async function encryptText() {
  const plain = document.getElementById("plainText").value;
  const res = await fetch(`${API}/encrypt`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(plain)
  });
  const cipher = await res.text();
  document.getElementById("cipherText").value = cipher;
}

async function decryptText() {
  const cipher = document.getElementById("cipherText").value;
  const res = await fetch(`${API}/decrypt`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(cipher)
  });
  const plain = await res.text();
  document.getElementById("result").textContent = plain;
}
