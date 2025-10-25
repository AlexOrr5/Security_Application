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

  async function hybridEncrypt() {
  const message = document.getElementById("plainText").value;
  const res = await fetch("http://localhost:8080/api/rsa/hybrid/encrypt", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ message }),
  });
  const data = await res.json();
  document.getElementById("cipherText").value = JSON.stringify(data, null, 2);
}

async function hybridDecrypt() {
  const { encryptedKey, iv, cipherText } = JSON.parse(
    document.getElementById("cipherText").value
  );
  const res = await fetch("http://localhost:8080/api/rsa/hybrid/decrypt", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ encryptedKey, iv, cipherText }),
  });
  const text = await res.text();
  document.getElementById("result").textContent = text;
}

}
