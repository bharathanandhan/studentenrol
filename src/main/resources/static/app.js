const API_URL = "/api/students";

async function fetchStudents() {
  const res = await fetch(API_URL);
  const data = await res.json();
  const tbody = document.querySelector("#studentsTable tbody");
  tbody.innerHTML = "";
  data.forEach(student => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${student.id}</td>
      <td>${student.name}</td>
      <td>${student.email}</td>
      <td>${student.course}</td>
      <td>
        <button onclick="editStudent(${student.id})">Edit</button>
        <button onclick="deleteStudent(${student.id})">Delete</button>
      </td>`;
    tbody.appendChild(tr);
  });
}

async function saveStudent() {
  const id = document.getElementById("studentId").value;
  const name = document.getElementById("name").value.trim();
  const email = document.getElementById("email").value.trim();
  const course = document.getElementById("course").value.trim();

  if (!name || !email || !course) {
    alert("Please fill all fields");
    return;
  }

  const student = { name, email, course };
  let method = "POST";
  let url = API_URL;

  if (id) {
    method = "PUT";
    url += "/" + id;
  }

  const res = await fetch(url, {
    method,
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(student),
  });

  if (res.ok) {
    clearForm();
    fetchStudents();
  } else {
    alert("Failed to save student");
  }
}

async function editStudent(id) {
  const res = await fetch(API_URL + "/" + id);
  if (res.ok) {
    const student = await res.json();
    document.getElementById("studentId").value = student.id;
    document.getElementById("name").value = student.name;
    document.getElementById("email").value = student.email;
    document.getElementById("course").value = student.course;
  }
}

async function deleteStudent(id) {
  if (confirm("Are you sure to delete?")) {
    const res = await fetch(API_URL + "/" + id, { method: "DELETE" });
    if (res.ok) {
      fetchStudents();
    } else {
      alert("Failed to delete");
    }
  }
}

function clearForm() {
  document.getElementById("studentId").value = "";
  document.getElementById("name").value = "";
  document.getElementById("email").value = "";
  document.getElementById("course").value = "";
}

window.onload = fetchStudents;

