package com.t2kat.annur.controller;

import com.t2kat.annur.persistence.model.Beneficiario;
import com.t2kat.annur.persistence.model.Tomadores;
import com.t2kat.annur.persistence.service.BeneficiarioService;
import com.t2kat.annur.persistence.service.TomadoresService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class MiController {

    @GetMapping("/login")
    public String loginPage() {
        // Devuelve la vista del formulario de login

        System.out.println("vamos al login ");
        return "login";
    }
    @GetMapping("/logout")
    public String logoutPage() {
        // Devuelve la vista del formulario de login

        System.out.println("vamos al formulario CLOSE SESSION");
        return "login";
    }

    @RequestMapping("/login")
    public String login() {
       //autenticar si login es correcto
        System.out.println("session iniciada.");

        return "redirect:/";

    }

    @GetMapping("/")
    public String getTomadores(
            @RequestParam(value = "query", defaultValue = "") String query,
            @RequestParam(value = "polizaCaducada", defaultValue = "false") boolean polizaCaducada,
            @RequestParam(value = "verAvisados", required = false) Boolean verAvisados,
            @RequestParam(value = "verProximosAVencer", required = false) Boolean verProximosAVencer,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        System.out.println("polizaCaducada: " + polizaCaducada);
        System.out.println("verAvisados: " + verAvisados);
        System.out.println("verProximosAVencer: " + verProximosAVencer);

        Page<Tomadores> tomadoresPage = tomadoresService.buscarPorDniONombrePolizaCaducada(
                query, polizaCaducada, verAvisados, verProximosAVencer, page, size);

        model.addAttribute("tomadores", tomadoresPage);
        model.addAttribute("query", query);
        model.addAttribute("polizaCauducada", polizaCaducada);
        model.addAttribute("verAvisados", verAvisados);
        model.addAttribute("verProximosAVencer", verProximosAVencer);

        int totalPages = tomadoresPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("listaActual", obtenerDescripcionLista(verAvisados, verProximosAVencer, polizaCaducada));

        return "index";
    }

    private String obtenerDescripcionLista(Boolean verAvisados, Boolean verProximos, boolean caducada) {
        if (Boolean.TRUE.equals(verProximos)) {
            if (Boolean.TRUE.equals(verAvisados)) return "⏳✅ Mostrando: Próximos YA avisados";
            if (Boolean.FALSE.equals(verAvisados)) return "⏳❌ Mostrando: Próximos NO avisados";
            return "⏳ Mostrando: Próximos a vencer";
        }
        if (caducada) {
            if (Boolean.TRUE.equals(verAvisados)) return "✅ Mostrando: Caducados YA avisados";
            if (Boolean.FALSE.equals(verAvisados)) return "❌ Mostrando: Caducados NO avisados";
            return "🔥 Mostrando: Todos los caducados";
        }
        return "📋 Mostrando: Todos los tomadores";
    }


    @PostMapping("/avisos/enviar")
    public String enviarAvisos(@RequestParam boolean proximos, RedirectAttributes redirectAttributes) {
        tomadoresService.enviarAvisosMasivos(false, proximos);
        redirectAttributes.addFlashAttribute("mensaje", "✅ WhatsApp + Email enviados correctamente");
        return proximos
                ? "redirect:/?verProximosAVencer=true&verAvisados=false"
                : "redirect:/?polizaCaducada=true&verAvisados=false";
    }

    @PostMapping("/avisos/reenviar")
    public String reenviarAvisos(@RequestParam boolean proximos, RedirectAttributes redirectAttributes) {
        tomadoresService.enviarAvisosMasivos(true, proximos);
        redirectAttributes.addFlashAttribute("mensaje", "🔁  WhatsApp + Email reenviados correctamente");
        return proximos
                ? "redirect:/?verProximosAVencer=true&verAvisados=true"
                : "redirect:/?polizaCaducada=true&verAvisados=true";
    }




    @GetMapping("/formTomador")
    public String addTomadorView(Model model) {
        // Obtén la lista de tomadores
        System.out.println("addTomador View");
        model.addAttribute("tomador", new Tomadores());
        return "formTomador";
    }

    @RequestMapping(value = "tomador", method = RequestMethod.POST)
    public String saveTomador(Tomadores tomadores) {
        tomadoresService.saveTomador(tomadores);

        System.out.println("save Tomador con exito");
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editTomador(@PathVariable long id, Model model) {
        Tomadores tomador = tomadoresService.getTomadorById(id); // Recupera el tomador


        model.addAttribute("tomador", tomador);
        return "formTomador";
    }


    @GetMapping("/delete/{id}")
    public String deleteTomador(@PathVariable long id) {

        System.out.println("DELETE id "+id);
        tomadoresService.deleteTomadorById(id);
        //Eliminar tambien sus beneficiarios???¿?¿?¿?

        return "redirect:/";
    }

    @GetMapping("/beneficiarios/{id}")
    public String getBeneficiarios(@PathVariable long id,Model model) {
        System.out.println( "beneficiarios por tomador id "+id);
        List<Beneficiario> beneficiarios = beneficiarioService.listByTomadorId(id);

        System.out.println("size beneficiario  "+beneficiarios.size());
        model.addAttribute("beneficiarios", beneficiarios);
        model.addAttribute("tomador_id", id);
        System.out.println(" id tomador "+id);
        return "/beneficiarios";
    }
    @GetMapping("/formBeneficiario/{id}")
    public String addBeneficiario(@PathVariable long id,Model model) {
        // Obtén la lista de tomadores
        System.out.println("addBeneficiario al tomador id "+id);
        Beneficiario  beneficiario = new Beneficiario();
        beneficiario.setTomadorId(id);
        model.addAttribute("beneficiario", beneficiario);

        return "formBeneficiario";
    }
    @GetMapping("/beneficiario/edit/{id}")
    public String editBeneficiario(@PathVariable long id, Model model) {
        System.out.println("beneficiario "+id);
        model.addAttribute("beneficiario", beneficiarioService.getBeneficiarioById(id));

        return "formBeneficiario";
    }
    @RequestMapping(value = "beneficiario", method = RequestMethod.POST)
    public String saveBeneficiario(Beneficiario beneficiario) {
        System.out.println("tomador nombre "+beneficiario.getNombreBeneficiario());

        System.out.println("tomador id "+beneficiario.getTomadorId());
        beneficiarioService.saveBeneficiario(beneficiario);

        System.out.println("save Beneficiario con exito");
        return "redirect:/beneficiarios/"+beneficiario.getTomadorId();
    }
    @GetMapping("/beneficiario/delete/{id}/{id_tomador}")
    public String deleteBeneficiario(@PathVariable long id,@PathVariable long id_tomador) {

        System.out.println("DELETE Benenficiario id "+id);
        System.out.println("DELETE  id_tomador "+id_tomador);

        //beneficiarioService.deleteBeneficiarioById(id);

        return "redirect:/beneficiarios/"+id_tomador;
    }

    @GetMapping("/beneficiariosMayores")
    public String getBeneficiariosMayoresgetTomadores(
            @RequestParam(value = "query", defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,Model model) {
        System.out.println("beneficiarios Mayores..");
        Page<Beneficiario> beneficiarios = beneficiarioService.getMayoresDeEdad(query,page, size);
        model.addAttribute("query", query);
        model.addAttribute("beneficiarios", beneficiarios);
        int totalPages = beneficiarios.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "/beneficiariosMayores";
    }


    @GetMapping("/ficha/{id}")
    public void generarFichaPDF(@PathVariable Long id, HttpServletResponse response) throws IOException {
        try {
            tomadoresService.generarFichaPDF(id, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generando PDF");
        }
    }

    @Autowired
    private JavaMailSender sender;

    @Autowired
    private TomadoresService tomadoresService;

    @Autowired
    private BeneficiarioService beneficiarioService;



}

