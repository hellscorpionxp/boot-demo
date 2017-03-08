package com.example;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@Controller
public class BootDemoApplication {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private PersonRepository personRepository;

    public static void main(String[] args) {
        SpringApplication.run(BootDemoApplication.class, args);
    }

    @RequestMapping("/")
    public String index(Model model) {
        Person single = new Person(null, "single", 11, null);
        List<Person> persons = new ArrayList<>();
        Person p1 = new Person(null, "person1", 11, null);
        Person p2 = new Person(null, "person2", 22, null);
        Person p3 = new Person(null, "person3", 33, null);
        persons.add(p1);
        persons.add(p2);
        persons.add(p3);
        model.addAttribute("singlePerson", single);
        model.addAttribute("people", persons);
        return "index";
    }

    // 广播式
    @MessageMapping("/welcome")
    @SendTo("/topic/getResponse")
    public ExampleResponse say(ExampleMessage exampleMessage) throws InterruptedException {
        Thread.sleep(5000);
        return new ExampleResponse("Welcome, " + exampleMessage.getName());
    }

    // 点对点式
    @MessageMapping("/chat")
    public void handleChat(Principal principal, String msg) {
        if (principal.getName().equals("wy")) {
            messagingTemplate.convertAndSendToUser("example", "/queue/notifications",
                    principal.getName() + "-send:" + msg);
        } else {
            messagingTemplate.convertAndSendToUser("wy", "/queue/notifications", principal.getName() + "-send:" + msg);
        }
    }

    @RequestMapping("/ws")
    public String ws() {
        return "ws";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/chat")
    public String chat() {
        return "chat";
    }

    @RequestMapping("/save")
    @ResponseBody
    public Person save(String name, String address, Integer age) {
        Person p = personRepository.save(new Person(null, name, age, address));
        return p;
    }

    @RequestMapping("/q1")
    @ResponseBody
    public List<Person> q1(String address) {
        return personRepository.findByAddress(address);
    }

    @RequestMapping("/q2")
    @ResponseBody
    public Person q2(String name, String address) {
        return personRepository.findByNameAndAddress(name, address);
    }

    @RequestMapping("/q3")
    @ResponseBody
    public Person q3(String name, String address) {
        return personRepository.withNameAndAddressQuery(name, address);
    }

    @RequestMapping("/q4")
    @ResponseBody
    public Person q4(String name, String address) {
        return personRepository.withNameAndAddressNamedQuery(name, address);
    }

    @RequestMapping("/sort")
    @ResponseBody
    public List<Person> sort() {
        return personRepository.findAll(new Sort(Direction.ASC, "age"));
    }

    @RequestMapping("/page")
    @ResponseBody
    public Page<Person> page() {
        return personRepository.findAll(new PageRequest(1, 2));
    }

    @RequestMapping(value = "/person", produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public List<Person> findAll() {
        return personRepository.findAll();
    }

}
