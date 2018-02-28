package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.AdminUserDTO;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.EmailExistsException;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.UserService;

import javax.validation.Valid;

@Controller
public class MainController extends WebMvcConfigurerAdapter {

    @Autowired
    private Backend backend;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/about");
        registry.addViewController("/download");
        registry.addViewController("/login");
    }

    /**
     * Handler for taking out and returning device scans.
     * @param scan A JSON containing device and user strings.
         * @return An HTTP status code and body description of error or action performed.
     */
    @PatchMapping(value = "/devices", consumes = "application/json", produces = "text/plain")
    @ResponseBody
    public ResponseEntity changeDeviceState(@RequestBody Scan scan) {
        try {
            ScanReturn result = backend.scanReceived(scan);
            switch (result) {
                case SUCCESSRETURN:
                    return ResponseEntity.status(200).body("Device " + scan.getDevice() + " successfully returned by " + scan.getUser() + ".");
                case SUCCESSREMOVAL:
                    return ResponseEntity.status(200).body("Device " + scan.getDevice() + " successfully taken out by " + scan.getUser() + ".");
                case SUCCESSRETURNANDREMOVAL:
                    return ResponseEntity.status(200).body("Device " + scan.getDevice() + " successfully returned and taken out by " + scan.getUser() + ".");
                case FAILUSERNOTRECOGNISED:
                    return ResponseEntity.status(500).body("User not recognised");
                case FAILDEVICENOTRECOGNISED:
                    return ResponseEntity.status(500).body("Device not recognised");
                case FAILUSERATDEVICELIMIT:
                    return ResponseEntity.status(403).body("User " + scan.getUser() + " is at their limit of removable devices.");
                case FAILUSERNORPERMITTEDTOREMOVE:
                    return ResponseEntity.status(403).body("User " + scan.getUser() + " is not permitted to remove");
                case FAILDEVICEUNAVIALABLE:
                    return ResponseEntity.status(403).body("Device " + scan.getDevice() + " can not be taken out.");
                case ERRORCONNECTIONFAILED:
                    return ResponseEntity.status(500).body("Error connecting to database.");
                case ERRORUSERNOTLOADED:
                    return ResponseEntity.status(403).body("User " + scan.getUser() + " not recognised.");
                case ERRORDEVICENOTLOADED:
                    return ResponseEntity.status(403).body("Device " + scan.getDevice() + " not recognised.");
                case ERRORDEVICEHANDLINGFAILED:
                    return ResponseEntity.status(500).body("Error handling device return or takeout.");
                case ERRORRETURNFAILED:
                    return ResponseEntity.status(500).body("Error returning device.");
                case ERRORREMOVALFAILED:
                    return ResponseEntity.status(500).body("Error taking out device.");
                default:
                    throw new Exception();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Unknown server error.");
        }
    }


    @GetMapping(value = "/devices/{id}", produces = "application/json")
    @ResponseBody
    public DeviceState checkIfDeviceOut(@PathVariable String id) {

        return null;
    }

    @RequestMapping(value = "/user/registration", method = RequestMethod.GET)
    public String showRegistrationForm(WebRequest request, Model model) {
        AdminUserDTO userDto = new AdminUserDTO();
        model.addAttribute("user", userDto);
        return "registration";
    }

    public ModelAndView registerUserAccount(
            @ModelAttribute("user") @Valid AdminUserDTO accountDTO,
            BindingResult result, WebRequest request, Errors errors) {

        AdminUser registered = new AdminUser();
        if (!result.hasErrors()) {
            registered = createUserAccount(accountDTO, result);
        }
        if (registered == null) {
            result.rejectValue("email", "message.regError");
        }
        if (registered == null) {
            result.rejectValue("email", "message.regError");
        }
        if (result.hasErrors()) {
            return new ModelAndView("registration", "user", accountDTO);
        }
        else {
            return new ModelAndView("successRegister", "user", accountDTO);
        }
    }

    @Autowired
    UserService userService;

    private AdminUser createUserAccount(AdminUserDTO accountDTO, BindingResult result) {
        AdminUser registered = null;
        try {
            registered = userService.registerNewUserAccount(accountDTO);
        } catch (EmailExistsException e) {
            return null;
        }
        return registered;
    }

    @RequestMapping("/dashboard")
    public String dashboard(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        model.addAttribute("name", name);
        return "dashboard";
    }
}
