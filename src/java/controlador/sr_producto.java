package controlador;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.util.HashMap;
import modelo.Producto;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class sr_producto extends HttpServlet {

    Producto producto;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        producto = new Producto();
        String accion = request.getParameter("btn");

        if (accion != null) {

            // 🧩 Datos del formulario
            producto.setProducto(request.getParameter("txt_producto"));
            producto.setId_marca(Integer.parseInt(request.getParameter("drop_marca")));
            producto.setDescripcion(request.getParameter("txt_descripcion"));
            producto.setPrecio_costo(Double.parseDouble(request.getParameter("txt_costo")));
            producto.setPrecio_venta(Double.parseDouble(request.getParameter("txt_venta")));
            producto.setExistencia(Integer.parseInt(request.getParameter("txt_existencia")));

            // 📁 Ruta real dentro del proyecto (donde está assets/img/productos)
            String uploadPath = getServletContext().getRealPath("/assets/img/productos/");
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            // 📸 Manejo de imagen
            String nombreArchivo = "";
            String rutaRelativa = "";
            Part filePart = request.getPart("file_imagen");

            if (filePart != null && filePart.getSize() > 0) {
                // 🟢 Si se sube una nueva imagen
                nombreArchivo = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                filePart.write(uploadPath + File.separator + nombreArchivo);
                rutaRelativa = "assets/img/productos/" + nombreArchivo;
            } else {
                // 🟡 Si no se sube una nueva, usar la imagen actual
                rutaRelativa = request.getParameter("txt_imagen_actual");
            }

            producto.setImagen_url(rutaRelativa);

            HashMap<String, String> resultado = new HashMap<>();

            switch (accion) {
                case "Agregar":
                    resultado = producto.insertar();
                    break;

                case "Actualizar":
                    if (request.getParameter("id_producto") != null) {
                        producto.setId_producto(Integer.parseInt(request.getParameter("id_producto")));
                        resultado = producto.actualizar();
                    }
                    break;

                case "Eliminar":
                    if (request.getParameter("id_producto") != null) {
                        producto.setId_producto(Integer.parseInt(request.getParameter("id_producto")));
                        resultado = producto.eliminar();
                    }
                    break;
            }

            // 🔁 Redirigir a la vista de productos
            response.sendRedirect("views/productos.jsp");

        } else {
            try (PrintWriter out = response.getWriter()) {
                out.println("<h3>No se recibió ninguna acción válida.</h3>");
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet para el manejo de productos con subida de imágenes";
    }
}
