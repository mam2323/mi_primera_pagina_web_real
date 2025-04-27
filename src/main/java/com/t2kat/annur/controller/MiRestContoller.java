package com.t2kat.annur.controller;

import com.t2kat.annur.persistence.model.Tomadores;
import com.t2kat.annur.persistence.service.TomadoresService;
import com.t2kat.annur.util.Utilidades;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController

public class MiRestContoller {

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        // Datos de ejemplo
        System.out.println("exportando excell...");

        // Generar el Excel y enviarlo como respuesta
        exportFichaToExcel(response, tomadoresService.list());
    }

    //mover a otra clase
    public static void exportFichaToExcel(HttpServletResponse response, List<Tomadores> tomadoresList) throws IOException {
        /*
         * Obtener los diagnósticos para mostrarlos en un Excel
         */

        // Creación del documento Excel
        Workbook wb = new HSSFWorkbook();
        // 1ª hoja: Información de Empresa y Diagnóstico
        Sheet sheetExpedientes = wb.createSheet("Todos expedientes");
        String[] headers = { /* 0 */ "#####","Expediente	","Nombre Completo	", /* 1 */ "DNI/Pasaporte	", "Fecha Inicio  	", "Fecha Fin  ",
                "Teléfono		", "Dirección		", "Precio (€)	" };

        // Escribimos las cabeceras de la 1ª hoja
        Row rowDiagnostico = sheetExpedientes.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            rowDiagnostico.createCell(i).setCellValue(headers[i]);
        }

        int numRowExpedientes = 1;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        for (Tomadores t : tomadoresList) {
            rowDiagnostico = sheetExpedientes.createRow(numRowExpedientes);
            rowDiagnostico.createCell(0).setCellValue(numRowExpedientes);
            rowDiagnostico.createCell(1).setCellValue(t.getExpediente().toUpperCase());
            rowDiagnostico.createCell(2)
                    .setCellValue(t.getNombre().toUpperCase() + " " + t.getApellido().toUpperCase());
            rowDiagnostico.createCell(3).setCellValue(t.getIdentificacion().toUpperCase());
            rowDiagnostico.createCell(4).setCellValue(t.getFechaInicio());
            rowDiagnostico.createCell(5).setCellValue(t.getFechaFin());
            rowDiagnostico.createCell(6).setCellValue(t.getTelefono());
            rowDiagnostico.createCell(7).setCellValue(t.getDireccion());
            rowDiagnostico.createCell(8).setCellValue(t.getTasas()+"   €");

            numRowExpedientes++;
        }

        rowDiagnostico = sheetExpedientes.createRow(numRowExpedientes);

        // Ajustamos el ancho de todas las celdas de la hoja
        for (int i = 0; i < headers.length; i++) {
            sheetExpedientes.autoSizeColumn(i);
        }

        String date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String nombreFichero = "TodoExpedientes-" + date + ".xls";

        // Write the output and show
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + nombreFichero);

        OutputStream out = response.getOutputStream();

        try {
            wb.write(out);
        } catch (Exception e) {
            // Nothing
        }
        out.close();
        // wb.close();
    }
    @Autowired
    private TomadoresService tomadoresService;

}
