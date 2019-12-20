/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.studiomascia.gestionale.controllers;

import it.studiomascia.gestionale.models.AnagraficaSocieta;
import it.studiomascia.gestionale.models.DBFile;
import it.studiomascia.gestionale.models.Ddt;
import it.studiomascia.gestionale.models.Pagamento;
import it.studiomascia.gestionale.models.XmlFatturaBase;
import it.studiomascia.gestionale.repository.AnagraficaSocietaRepository;
import it.studiomascia.gestionale.repository.DBFileRepository;
import it.studiomascia.gestionale.repository.DdtRepository;
import it.studiomascia.gestionale.repository.XmlFatturaBaseRepository;
import it.studiomascia.gestionale.service.DBFileStorageService;
import it.studiomascia.gestionale.xml.FatturaElettronicaType;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.text.SimpleDateFormat;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 *
 * @author luigi
 */
@Controller
public class DdtController {
    
    private static final Logger logger = LoggerFactory.getLogger(DdtController.class);

    private SimpleDateFormat formattaData = new SimpleDateFormat("dd-MM-yyyy");

    @Autowired
    private DBFileStorageService DBFileStorageService;

    @Autowired
    private DdtRepository ddtRepository;
    
    @Autowired
    private XmlFatturaBaseRepository xmlFatturaRepository;
    
    @Autowired
    private AnagraficaSocietaRepository anagraficaSocietaRepository;
    
    @GetMapping("/InvoiceIn/{fatturaId}/DDT")
    public String DdtFattura(Model model, @PathVariable String fatturaId){
        List<String> headers = new  ArrayList<>();
            headers.add("Id");
            headers.add("Registro IVA");
            headers.add("N. Fattura");
            headers.add("Data Fattura");
            headers.add("P.IVA");
            headers.add("Denominazione");
            headers.add("Imponibile");
            
            String strData="N/A";
            Integer id = Integer.valueOf(fatturaId);
            XmlFatturaBase xmlFattura = xmlFatturaRepository.findById(id).get();
            try {
                byte[] byteArr = xmlFattura.getXmlData().getBytes("UTF-8");
                StringWriter sw = new StringWriter();
                JAXBContext context = JAXBContext.newInstance(FatturaElettronicaType.class);
                // Unmarshaller serve per convertire il file in un oggetto
                Unmarshaller jaxbUnMarshaller = context.createUnmarshaller();
                // Marshaller serve per convertire l'oggetto ottenuto dal file in una stringa xml
                Marshaller jaxbMarshaller = context.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
                JAXBElement<FatturaElettronicaType> root =jaxbUnMarshaller.unmarshal(new StreamSource(new ByteArrayInputStream(byteArr)), FatturaElettronicaType.class);
                FatturaElettronicaType item = root.getValue();
                jaxbMarshaller.marshal(root, sw);

                Date dataFattura = item.getFatturaElettronicaBody().get(0).getDatiGenerali().getDatiGeneraliDocumento().getData().toGregorianCalendar().getTime();
                String numeroFattura= item.getFatturaElettronicaBody().get(0).getDatiGenerali().getDatiGeneraliDocumento().getNumero();
                String importoFattura= item.getFatturaElettronicaBody().get(0).getDatiGenerali().getDatiGeneraliDocumento().getImportoTotaleDocumento().toString();
                String partitaIVA =  item.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getIdFiscaleIVA().getIdCodice();
                String denominazione = item.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getDenominazione();
            
                Map<String, Object> riga = new HashMap<String, Object>();
                riga.put("Id", xmlFattura.getId());   
                strData = ((xmlFattura.getDataRegistrazione() == null)) ? "N/A" : formattaData.format(xmlFattura.getDataRegistrazione());
                riga.put("Registro IVA",xmlFattura.getNumeroRegistrazione()+ " - " +  strData);
                riga.put("N. Fattura", numeroFattura);
                riga.put("Data Fattura", formattaData.format(dataFattura));
                riga.put("P.IVA",partitaIVA );
                riga.put("Denominazione",denominazione );
                riga.put("Imponibile", importoFattura);
             
                model.addAttribute("fattura", riga);
                model.addAttribute("headers", headers);
 
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }     
            
        Set<Ddt> listaDDT= xmlFattura.getDDT();
        
        List<DBFile> listaFile = new ArrayList<DBFile>();
        for (Ddt x : listaDDT){

            listaFile.addAll(x.getFilesDDT());
        	
        }
        
        List lista = new ArrayList(listaDDT);
        model.addAttribute("listafiles", listaFile);
        model.addAttribute("listapagamenti", lista);
        return "lista_ddt_fattura";
    }
    
   
    @GetMapping("/Provider/{idProvider}/ModalDdt")
    public String ModalAddDdtProvider (ModelMap model,@PathVariable Integer idProvider){
        
        AnagraficaSocieta provider = anagraficaSocietaRepository.findById(idProvider).get();
        Ddt ddt = new Ddt();
        model.addAttribute("ddt",ddt);  
        model.addAttribute("provider",provider);  
        return "modalContents :: ddtProvider";
    }
 
    
    @PostMapping("/Provider/{idProvider}/ModalDdt")
    public String registraNuovoPagamentoFatturaIn( @ModelAttribute("ddt") Ddt ddt, Model model,@PathVariable Integer idProvider, RedirectAttributes redirectAttributes)
    {
        AnagraficaSocieta provider = anagraficaSocietaRepository.findById(idProvider).get();
        ddt.setCreatore(SecurityContextHolder.getContext().getAuthentication().getName());
        ddt.setAnagraficaSocieta(provider);
        
        ddtRepository.save(ddt);

        //provider.getListaDDT().add(ddt);
        
        //anagraficaSocietaRepository.save(provider);
        return "redirect:/Provider/"+ idProvider +"/DDT";
    }
    

//    
//    @PostMapping("/Provider/{id}/ModalDdt")
//    public String registraNuovoPagamentoFatturaIn( @ModelAttribute("ddt") Ddt ddt, Model model,@PathVariable Integer idProvider, RedirectAttributes redirectAttributes)
//    {
//        AnagraficaSocieta provider = anagraficaSocietaRepository.findById(idProvider).get();
//        ddt.setCreatore(SecurityContextHolder.getContext().getAuthentication().getName());
//        provider.getPagamenti().add(pagamento);
//        
//        xmlFatturaBaseRepository.save(vecchiaFattura);
////        redirectAttributes.addFlashAttribute("messaggio","Pagamento inserito");  
//        return "redirect:/InvoiceIn/"+ id +"/Payments";
//    }
    
    
    

}
