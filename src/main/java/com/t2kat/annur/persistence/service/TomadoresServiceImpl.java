package com.t2kat.annur.persistence.service;


import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.t2kat.annur.persistence.model.Beneficiario;
import com.t2kat.annur.persistence.model.Tomadores;
import com.t2kat.annur.persistence.repository.TomadoresRepository;
import com.t2kat.annur.util.Utilidades;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class TomadoresServiceImpl implements TomadoresService {

    @Autowired
    private TomadoresRepository tomadoresRepository;

    public List<Tomadores> list() {
        return tomadoresRepository.findAll();
    }

    @Override
    public Tomadores getTomadorById(long id) {
        return tomadoresRepository.findById(id).get();
    }
    @Override
    public void deleteTomadorById(long id) {
         tomadoresRepository.deleteById(id);
    }

    @Override
    public Page<Tomadores> listPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return tomadoresRepository.findAll(pageable);
    }


    @Override
    public Tomadores saveTomador(Tomadores tomador) {
        return tomadoresRepository.save(tomador);
    }

    @Override
    public Page<Tomadores> buscarPorDniONombrePolizaCaducada(String query,
                                                             boolean polizaCaducada,
                                                             Boolean verAvisados,
                                                             Boolean verProximosAVencer,
                                                             int page,
                                                             int size) {

        Pageable pageable = PageRequest.of(page, size);
        LocalDate hoy = LocalDate.now();
        LocalDate en30dias = hoy.plusDays(30);

        // ⏳ FILTROS DE PRÓXIMOS A VENCER
        if (Boolean.TRUE.equals(verProximosAVencer)) {
            if (verAvisados == null) {
                return tomadoresRepository.findByFechaFinBetween(hoy, en30dias, pageable);
            } else if (verAvisados) {
                return tomadoresRepository.findByFechaFinBetweenAndAvisoEnviadoTrue(hoy, en30dias, pageable);
            } else {
                return tomadoresRepository.findByFechaFinBetweenAndAvisoEnviadoFalse(hoy, en30dias, pageable);
            }
        }

        // 🔥 FILTROS DE CADUCADOS
        if (polizaCaducada) {
            if (verAvisados == null) {
                return tomadoresRepository.findByFechaFinBefore(hoy, pageable);
            } else if (verAvisados) {
                return tomadoresRepository.findByFechaFinBeforeAndAvisoEnviadoTrue(hoy, pageable);
            } else {
                return tomadoresRepository.findByFechaFinBeforeAndAvisoEnviadoFalse(hoy, pageable);
            }
        }

        // 🔎 BÚSQUEDA SIMPLE
        if (!query.isEmpty()) {
            return tomadoresRepository.findByNombreOrApellidoOrIdentificacionOrTelefonoOrDireccion(
                    query, query, query, query, query, pageable);
        }

        return tomadoresRepository.findAll(pageable);
    }
    @Override
    public void enviarAvisosMasivos(boolean reenviar, boolean proximosAVencer) {
        LocalDate hoy = LocalDate.now();
        LocalDate en30dias = hoy.plusDays(30);
        List<Tomadores> lista;

        if (proximosAVencer) {
            lista = reenviar
                    ? tomadoresRepository.findByFechaFinBetweenAndAvisoEnviadoTrue(hoy, en30dias, Pageable.unpaged()).getContent()
                    : tomadoresRepository.findByFechaFinBetweenAndAvisoEnviadoFalse(hoy, en30dias, Pageable.unpaged()).getContent();
        } else {
            lista = reenviar
                    ? tomadoresRepository.findByFechaFinBeforeAndAvisoEnviadoTrue(hoy, Pageable.unpaged()).getContent()
                    : tomadoresRepository.findByFechaFinBeforeAndAvisoEnviadoFalse(hoy, Pageable.unpaged()).getContent();
        }

        for (Tomadores t : lista) {
            try {
                // ✅ Envío de EMAIL
                MimeMessage message = sender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(t.getCorreo());
                helper.setSubject("AVISO: Póliza ");

                String cuerpo;
                if (t.getFechaFin().isBefore(hoy)) {
                    cuerpo = "<html><body>" +
                            "<h4>Estimado " + t.getNombre() + " " + t.getApellido() + "</h4>" +
                            "<p>Le informamos que su póliza ya ha vencido.</p>" +
                            "<p>Le recordamos que debe renovarla para mantener su cobertura.</p>" +
                            "<p>De no hacerlo, procederemos a la baja automática.</p>" +
                            "<p>Atentamente,</p>" +
                            "</body></html>";
                } else {
                    cuerpo = "<html><body>" +
                            "<h4>Estimado " + t.getNombre() + " " + t.getApellido() + "</h4>" +
                            "<p>Su póliza está a punto de vencer el <strong>" + t.getFechaFin() + "</strong>.</p>" +
                            "<p>Le recomendamos renovarla cuanto antes.</p>" +
                            "<p>Atentamente </p>" +
                            "</body></html>";
                }

                helper.setText(cuerpo, true);
                sender.send(message);

                // ✅ Envío de WHATSAPP
                if (t.getTelefono() != null && !t.getTelefono().isEmpty()) {
                    if (t.getFechaFin().isBefore(hoy)) {
                        enviarWhatsappConPlantilla(t, "plantilla_caducados");
                    } else {
                        enviarWhatsappConPlantilla(t, "plantilla_futuro_caducado");
                    }
                }

                // ✅ Marcar como enviado si no es reenvío
                if (!reenviar) {
                    t.setAvisoEnviado(true);
                    tomadoresRepository.save(t);
                }

            } catch (Exception e) {
                System.out.println("❌ Error al enviar a " + t.getCorreo() + ": " + e.getMessage());
            }
        }
    }


    public void enviarWhatsappConPlantilla(Tomadores t, String plantillaNombre) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(twilioAccountSid, twilioAuthToken);  // Aquí usas las variables del archivo properties
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<Map<String, String>> parametros = new ArrayList<>();
        parametros.add(Map.of("type", "text", "text", t.getNombre()));
        parametros.add(Map.of("type", "text", "text", t.getApellido()));

        if (plantillaNombre.equals("plantilla_futuro_caducado")) {
            parametros.add(Map.of("type", "text", "text", t.getFechaFin().toString()));
        }

        Map<String, Object> payload = Map.of(
                "to", "whatsapp:+34" + t.getTelefono(),       // Número de destino con el prefijo 'whatsapp:+34'
                "from", twilioFrom,                           // El número de Twilio que registraste en WhatsApp
                "template", Map.of(
                        "name", plantillaNombre,
                        "language", Map.of("code", "es"),
                        "components", List.of(Map.of(
                                "type", "body",
                                "parameters", parametros
                        ))
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        String url = "https://api.twilio.com/2010-04-01/Accounts/" + twilioAccountSid + "/Messages.json";  // Usamos el SID aquí

        try {
            restTemplate.postForEntity(url, request, String.class);
            System.out.println("✅ WhatsApp enviado a: " + t.getTelefono());
        } catch (Exception e) {
            System.out.println("❌ Error al enviar WhatsApp: " + e.getMessage());
        }
    }




    @Override
    public void generarFichaPDF(Long id, HttpServletResponse response) {
        try {
            Tomadores tomador = tomadoresRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Tomador no encontrado"));

            PdfReader reader = new PdfReader("src/main/resources/templates/modelo_annur.pdf"); // Ruta al PDF modelo
            PdfStamper stamper = new PdfStamper(reader, response.getOutputStream());
            AcroFields form = stamper.getAcroFields();

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=ficha-" + tomador.getApellido() + ".pdf");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            form.setField("expediente", tomador.getExpediente());
            form.setField("nombre", tomador.getNombre().toUpperCase() + " " + tomador.getApellido().toUpperCase());
            form.setField("ident", tomador.getIdentificacion());
            form.setField("nacimiento", sdf.format(java.sql.Date.valueOf(tomador.getFechaNacimiento())));
            form.setField("telefono", tomador.getTelefono());
            form.setField("correo", tomador.getCorreo());
            form.setField("direccion", tomador.getDireccion());
            form.setField("civil", Utilidades.getEstado(tomador.getCivil()));
            form.setField("anos_cubiertos", sdf.format(java.sql.Date.valueOf(tomador.getFechaFin())));

            // la parte del ppdf de Beneficiarios

            List<Beneficiario> beneficiarios = beneficiarioService.listByTomadorId(tomador.getId()); // Obtener todos los beneficiarios del tomador

// ------- Cónyuge (parentesco = 1)
            beneficiarios.stream()
                    .filter(b -> b.getParentesco() != null && b.getParentesco() == 1)
                    .findFirst()
                    .ifPresent(conyuge -> {
                        try {
                            form.setField("nombre_conyuge", conyuge.getNombreBeneficiario().toUpperCase() + " " + conyuge.getApellidoBeneficiario().toUpperCase()); // Nombre y apellido en mayúsculas
                        } catch (Exception e) {
                            throw new RuntimeException("Error al insertar cónyuge en PDF", e);
                        }
                    });

// ------- Padre/Madre (parentesco = 4)
            beneficiarios.stream()
                    .filter(b -> b.getParentesco() != null && b.getParentesco() == 4)
                    .findFirst()
                    .ifPresent(padre -> {
                        try {
                            form.setField("nombre_padre", padre.getNombreBeneficiario().toUpperCase() + " " + padre.getApellidoBeneficiario().toUpperCase());
                        } catch (Exception e) {
                            throw new RuntimeException("Error al insertar padre/madre en PDF", e);
                        }
                    });

// ------- Número hijos menores (parentesco = 2)
            long hijosMenores = beneficiarios.stream()
                    .filter(b -> b.getParentesco() != null && b.getParentesco() == 2)
                    .count();
            form.setField("nombre_hijos", String.valueOf(hijosMenores)); // Solo el número, no nombres

// ------- Número hijas solteras (parentesco = 3)
            long hijasSolteras = beneficiarios.stream()
                    .filter(b -> b.getParentesco() != null && b.getParentesco() == 3)
                    .count();
            form.setField("nombre_hijas", String.valueOf(hijasSolteras)); // Solo el número, no nombres


            stamper.close(); // Cierra el estampador para guardar el PDF
            reader.close();  // Cierra el lector del PDF
        } catch (Exception e) {
            throw new RuntimeException("Error generando ficha PDF", e); // Captura y lanza cualquier excepción
        }
    }


    @Autowired
    private BeneficiarioService beneficiarioService;


    @Autowired
    private JavaMailSender sender;

//    @Value("${twilio.phone.number}")
  //   private String twilioPhoneNumber;


    @Value("${twilio.account.sid}")
    private String twilioAccountSid;

    @Value("${twilio.auth.token}")
    private String twilioAuthToken;

    @Value("${twilio.phone.whatsapp}")
    private String twilioFrom;

}

