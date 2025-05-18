// ===== AUTENTICACIÓN JWT =====
function guardarToken(token) {
  localStorage.setItem("token", token);
}

function obtenerToken() {
  return localStorage.getItem("token");
}

function eliminarToken() {
  localStorage.removeItem("token");
}

function estaLogueado() {
  return !!obtenerToken();
}

function cerrarSesion() {
  eliminarToken();
  window.location.href = "/login.html";
}

// ===== FETCH CON AUTORIZACIÓN =====
async function fetchAutenticado(url, opciones = {}) {
  const token = obtenerToken();
  const headers = opciones.headers || {};

  if (token) {
    headers["Authorization"] = "Bearer " + token;
  }

  return fetch(url, {
    ...opciones,
    headers
  });
}

// ===== FORMATEO DE FECHAS =====
function formatearFecha(fechaISO) {
  const fecha = new Date(fechaISO);
  return fecha.toLocaleDateString("es-CO", {
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
  });
}

// ===== MOSTRAR MIS RESERVAS DINÁMICAMENTE =====
document.addEventListener("DOMContentLoaded", function () {
  const contenedor = document.getElementById("contenedor-reservas");

  if (contenedor && estaLogueado()) {
    fetchAutenticado("/api/reservas/mis-reservas")
      .then(response => {
        if (!response.ok) throw new Error("No autorizado");
        return response.json();
      })
      .then(reservas => {
        contenedor.innerHTML = "";

        reservas.forEach(r => {
          const card = `
            <div class="col-md-6 col-lg-4">
              <div class="reserva-card">
                <div class="reserva-header">
                  <h5>${r.tituloEvento}</h5>
                  <span class="reserva-fecha">${formatearFecha(r.fechaInicio)}</span>
                </div>
                <p><i class="bi bi-geo-alt"></i> ${r.auditorioNombre}</p>
                <p><i class="bi bi-clock"></i> ${r.fechaInicio.slice(11, 16)} - ${r.fechaFin.slice(11, 16)}</p>
                <p><i class="bi bi-people-fill"></i> ${r.cantidadPersonas} personas</p>
                <p><strong>Estado:</strong> ${r.estado}</p>
              </div>
            </div>
          `;
          contenedor.insertAdjacentHTML("beforeend", card);
        });
      })
      .catch(err => {
        console.error(err);
        alert("Error cargando reservas. Asegúrate de haber iniciado sesión.");
      });
  }
});
