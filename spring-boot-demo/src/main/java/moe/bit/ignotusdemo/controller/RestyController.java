package moe.bit.ignotusdemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import moe.bit.ignotusdemo.model.vo.FAuthInfo;
import moe.bit.ignotusdemo.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class RestyController {
    @Autowired
    AuthenticationService authenticationService;

    private ObjectMapper mapper = JsonMapper.builder()
        .addModule(new ParameterNamesModule())
        .addModule(new Jdk8Module())
        .addModule(new JavaTimeModule())
        .addModule(new KotlinModule())
        .build();

    @ResponseBody()
    @PostMapping("_preserved/authenticate_with_header")
    public ResponseEntity<String> register(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        FAuthInfo authInfo = authenticationService.authenticateWithHeader(token);
        String ret = "";
        if (authInfo != null) {
            try {
                ret = mapper.writeValueAsString(authInfo);
            } catch (Exception ignored) {
                ret = "";
            }
        }
        return new ResponseEntity<String>(ret, HttpStatus.OK);
    }
}
