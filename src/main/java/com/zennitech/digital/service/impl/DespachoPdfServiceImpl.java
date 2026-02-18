package com.zennitech.digital.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.zennitech.digital.pojo.DespachoDTO;
import com.zennitech.digital.pojo.DespachoDetalleDTO;
import com.zennitech.digital.repository.DespachoPdfRepository;
import com.zennitech.digital.service.DespachoPdfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class DespachoPdfServiceImpl implements DespachoPdfService {
    private final DespachoPdfRepository despachoPdfRepository;
    // Fuentes
    private static final Font FONT_TITLE = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font FONT_HEADER = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private static final Font FONT_NORMAL = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
    private static final Font FONT_SMALL = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
    private static final Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);

    // Colores
    private static final BaseColor COLOR_HEADER = new BaseColor(220, 220, 220);
    private static final BaseColor COLOR_BORDER = new BaseColor(100, 100, 100);

    @Override
    public ByteArrayOutputStream generarPdfDespacho(Long movimientoId) throws Exception {
        log.info("Generando PDF para movimiento ID: {}", movimientoId);

        // Obtener datos del despacho
        DespachoDTO despacho = obtenerDatosDespacho(movimientoId);

        if (despacho == null) {
            throw new RuntimeException("No se encontró el movimiento con ID: " + movimientoId);
        }

        // Generar PDF
        return generarPdfDesdeDTO(despacho);
    }

    @Override
    public ByteArrayOutputStream generarPdfDesdeDTO(DespachoDTO despacho) throws Exception {
        log.info("Generando PDF de despacho para: {}", despacho.getNombreTecnico());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Cambiar a orientación horizontal
        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            PdfContentByte canvas = writer.getDirectContent();

            // Configurar columnas (2 columnas en página horizontal)
            float marginLeft = 36; // 0.5 inch
            float marginRight = 36;
            float marginTop = 36;
            float marginBottom = 36;

            float pageWidth = PageSize.A4.rotate().getWidth() - marginLeft - marginRight;
            float pageHeight = PageSize.A4.rotate().getHeight() - marginTop - marginBottom;

            // Ancho de cada columna (con espacio entre ellas)
            float columnWidth = (pageWidth - 20) / 2; // 20px de espacio entre columnas

            // Primera columna (izquierda)
            float x1 = marginLeft;
            float y1 = marginTop + pageHeight;
            float x2 = x1 + columnWidth;
            float y2 = marginTop;

            // Segunda columna (derecha)
            float x3 = x2 + 20; // Espacio entre columnas
            float y3 = y1;
            float x4 = x3 + columnWidth;
            float y4 = y2;

            // Generar contenido para la primera columna
            ColumnText ct = new ColumnText(canvas);
            ct.setSimpleColumn(x1, y2, x2, y1);
            agregarContenidoDespacho(ct, despacho);
            ct.go();

            // Generar contenido para la segunda columna (mismo contenido)
            ct = new ColumnText(canvas);
            ct.setSimpleColumn(x3, y4, x4, y3);
            agregarContenidoDespacho(ct, despacho);
            ct.go();

            document.close();
            log.info("PDF horizontal con dos columnas generado exitosamente");

        } catch (Exception e) {
            log.error("Error al generar PDF", e);
            throw e;
        }

        return baos;
    }

    /**
     * Método para agregar todo el contenido del despacho a una columna
     */
    private void agregarContenidoDespacho(ColumnText ct, DespachoDTO despacho) throws DocumentException {
        // Agregar encabezado
        agregarEncabezadoColumna(ct, despacho);

        // Agregar información del despacho
        agregarInfoDespachoColumna(ct, despacho);

        // Agregar tabla de detalles
        agregarTablaDetallesColumna(ct, despacho);

        // Agregar firmas
        agregarFirmasColumna(ct, despacho);

        // Agregar pie de página
        agregarPiePaginaColumna(ct, despacho);
    }

    /**
     * Agrega el encabezado para una columna
     */
    private void agregarEncabezadoColumna(ColumnText ct, DespachoDTO despacho) throws DocumentException {
        // Tabla para el encabezado (2 columnas)
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{2, 1});

        // Columna izquierda - Información de la empresa
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setPaddingBottom(5);

        Paragraph empresa = new Paragraph(despacho.getEmpresa(), new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD));
        empresa.setAlignment(Element.ALIGN_CENTER);
        leftCell.addElement(empresa);

        Paragraph tipo = new Paragraph("TELECOMUNICACIONES", new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD));
        tipo.setAlignment(Element.ALIGN_CENTER);
        leftCell.addElement(tipo);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd / MM / yyyy");
        Paragraph fecha = new Paragraph("FECHA: " + despacho.getFecha().format(formatter), FONT_NORMAL);
        fecha.setAlignment(Element.ALIGN_CENTER);
        leftCell.addElement(fecha);

        headerTable.addCell(leftCell);

        // Columna derecha - RUC y Número de documento
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.BOX);
        rightCell.setBorderColor(COLOR_BORDER);
        rightCell.setPadding(3);

        Paragraph ruc = new Paragraph("R.U.C. " + despacho.getRuc(), new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL));
        ruc.setAlignment(Element.ALIGN_CENTER);
        rightCell.addElement(ruc);

        Paragraph tipoDoc = new Paragraph("NÚMERO DE DOCUMENTO", new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL));
        tipoDoc.setAlignment(Element.ALIGN_CENTER);
        rightCell.addElement(tipoDoc);

        Paragraph numDoc = new Paragraph(despacho.getNumeroDocumento(), new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD));
        numDoc.setAlignment(Element.ALIGN_CENTER);
        rightCell.addElement(numDoc);

        headerTable.addCell(rightCell);

        ct.addElement(headerTable);
        ct.addElement(new Paragraph(" "));

        // Título del documento
        Paragraph titulo = new Paragraph(despacho.getTitulo(), new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
        titulo.setAlignment(Element.ALIGN_CENTER);
        ct.addElement(titulo);
        ct.addElement(new Paragraph(" "));
    }

    /**
     * Agrega la información del despacho para una columna
     */
    private void agregarInfoDespachoColumna(ColumnText ct, DespachoDTO despacho) throws DocumentException {
        // Tabla para información del despacho (3 columnas)
        PdfPTable infoTable = new PdfPTable(new float[]{1, 1, 1});
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(5);

        // DNI
        PdfPCell dniCell = crearCeldaInfoColumna("DNI:", despacho.getDni());
        infoTable.addCell(dniCell);

        // CIUDAD
        PdfPCell ciudadCell = crearCeldaInfoColumna("CIUDAD:", despacho.getCiudad());
        infoTable.addCell(ciudadCell);

        // MOVIMIENTO
        PdfPCell movCell = crearCeldaInfoColumna("MOVIMIENTO:",
                despacho.getNumeroMovimiento() != null ? despacho.getNumeroMovimiento().toString() : "");
        infoTable.addCell(movCell);

        ct.addElement(infoTable);

        // PERSONAL (nombre completo del técnico)
        Paragraph personal = new Paragraph("PERSONAL: " + despacho.getNombreTecnico(), new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL));
        ct.addElement(personal);
        ct.addElement(new Paragraph(" "));
    }

    /**
     * Crea una celda para la información del despacho en columna
     */
    private PdfPCell crearCeldaInfoColumna(String label, String value) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingBottom(2);

        Paragraph p = new Paragraph();
        p.add(new Chunk(label + " ", new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD)));
        p.add(new Chunk(value, new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
        cell.addElement(p);

        return cell;
    }

    /**
     * Agrega la tabla de detalles para una columna
     */
    private void agregarTablaDetallesColumna(ColumnText ct, DespachoDTO despacho) throws DocumentException {
        // Tabla con 6 columnas ajustadas para el tamaño de columna
        PdfPTable table = new PdfPTable(new float[]{0.4f, 0.8f, 3f, 0.4f, 0.5f, 0.5f});
        table.setWidthPercentage(100);
        table.setSpacingAfter(10);

        // Encabezados con fuente más pequeña
        Font smallHeaderFont = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD);
        agregarCeldaEncabezadoColumna(table, "N°", smallHeaderFont);
        agregarCeldaEncabezadoColumna(table, "COD. SAP", smallHeaderFont);
        agregarCeldaEncabezadoColumna(table, "DESCRIPCIÓN", smallHeaderFont);
        agregarCeldaEncabezadoColumna(table, "(S)", smallHeaderFont);
        agregarCeldaEncabezadoColumna(table, "UM", smallHeaderFont);
        agregarCeldaEncabezadoColumna(table, "CANT.", smallHeaderFont);

        // Datos con fuente más pequeña
        Font smallDataFont = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL);
        for (DespachoDetalleDTO detalle : despacho.getDetalles()) {
            agregarCeldaDatoColumna(table, detalle.getNumero().toString(), Element.ALIGN_CENTER, smallDataFont);
            agregarCeldaDatoColumna(table, detalle.getCodigoSap(), Element.ALIGN_LEFT, smallDataFont);

            // Descripción con series
            PdfPCell descCell = new PdfPCell();
            descCell.setBorderColor(COLOR_BORDER);
            descCell.setPadding(2);

            Paragraph desc = new Paragraph(detalle.getDescripcion(), smallDataFont);
            descCell.addElement(desc);

            // Agregar series si existen
            if (detalle.getSeries() != null && !detalle.getSeries().isEmpty()) {
                descCell.addElement(new Paragraph(" ", smallDataFont));

                String[] series = detalle.getSeries().split(",");
                int count = 0;
                StringBuilder lineaSeries = new StringBuilder();

                for (String serie : series) {
                    if (count > 0 && count % 3 == 0) { // Menos series por línea para ajustar al ancho
                        descCell.addElement(new Paragraph(lineaSeries.toString().trim(),
                                new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL)));
                        lineaSeries = new StringBuilder();
                    }
                    lineaSeries.append(serie.trim()).append(" ");
                    count++;
                }

                if (lineaSeries.length() > 0) {
                    descCell.addElement(new Paragraph(lineaSeries.toString().trim(),
                            new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL)));
                }
            }

            table.addCell(descCell);

            agregarCeldaDatoColumna(table,
                    detalle.getUnidadMedida() != null && detalle.getUnidadMedida().equals("UN") ? "S" : "",
                    Element.ALIGN_CENTER, smallDataFont);
            agregarCeldaDatoColumna(table, detalle.getUnidadMedida(), Element.ALIGN_CENTER, smallDataFont);
            agregarCeldaDatoColumna(table, detalle.getCantidad().toString(), Element.ALIGN_CENTER, smallDataFont);
        }

        ct.addElement(table);
    }

    /**
     * Agrega celda de encabezado a la tabla para columna
     */
    private void agregarCeldaEncabezadoColumna(PdfPTable table, String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(COLOR_HEADER);
        cell.setBorderColor(COLOR_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(3);
        table.addCell(cell);
    }

    /**
     * Agrega celda de dato a la tabla para columna
     */
    private void agregarCeldaDatoColumna(PdfPTable table, String texto, int alignment, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBorderColor(COLOR_BORDER);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(2);
        table.addCell(cell);
    }

    /**
     * Agrega las firmas para una columna
     */
    private void agregarFirmasColumna(ColumnText ct, DespachoDTO despacho) throws DocumentException {
        ct.addElement(new Paragraph(" "));
        ct.addElement(new Paragraph(" "));

        // Tabla para las firmas (2 columnas)
        PdfPTable firmasTable = new PdfPTable(2);
        firmasTable.setWidthPercentage(100);
        firmasTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        // Línea para firma técnico
        PdfPCell tecnicoCell = new PdfPCell();
        tecnicoCell.setBorder(Rectangle.NO_BORDER);
        tecnicoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        tecnicoCell.setPaddingTop(20);

        Paragraph lineaTecnico = new Paragraph("_________________________",
                new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL));
        lineaTecnico.setAlignment(Element.ALIGN_CENTER);
        tecnicoCell.addElement(lineaTecnico);

        Paragraph labelTecnico = new Paragraph("TÉCNICO",
                new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD));
        labelTecnico.setAlignment(Element.ALIGN_CENTER);
        tecnicoCell.addElement(labelTecnico);

        firmasTable.addCell(tecnicoCell);

        // Línea para firma almacén
        PdfPCell almacenCell = new PdfPCell();
        almacenCell.setBorder(Rectangle.NO_BORDER);
        almacenCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        almacenCell.setPaddingTop(20);

        Paragraph lineaAlmacen = new Paragraph("_________________________",
                new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL));
        lineaAlmacen.setAlignment(Element.ALIGN_CENTER);
        almacenCell.addElement(lineaAlmacen);

        Paragraph labelAlmacen = new Paragraph("ALMACÉN",
                new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD));
        labelAlmacen.setAlignment(Element.ALIGN_CENTER);
        almacenCell.addElement(labelAlmacen);

        firmasTable.addCell(almacenCell);

        ct.addElement(firmasTable);
    }

    /**
     * Agrega el pie de página para una columna
     */
    private void agregarPiePaginaColumna(ColumnText ct, DespachoDTO despacho) throws DocumentException {
        ct.addElement(new Paragraph(" "));

        Paragraph footer = new Paragraph(
                "Base: Consultores - Gestión ERP                                                  página 1 de 1",
                new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL)
        );
        footer.setAlignment(Element.ALIGN_LEFT);
        ct.addElement(footer);
    }

    @Override
    public DespachoDTO obtenerDatosDespacho(Long movimientoId) {
        return despachoPdfRepository.obtenerDatosDespacho(movimientoId);
    }
}
