package io.wttech.habit.server.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@AllArgsConstructor
public class IndexController {

  private final ResourceLoader resourceLoader;

  @Operation(summary = "Catch all returning index HTML for React app deep linking")
  @GetMapping("/")
  public ResponseEntity<Resource> index(@Value("classpath:/public/index.html") final Resource indexHtml) {
    return ResponseEntity.ok(indexHtml);
  }

  @Operation(summary = "Catch all returning index HTML for React app deep linking")
  @GetMapping("/environments/**")
  public ResponseEntity<Resource> environmentDeepLink(@Value("classpath:/public/index.html") final Resource indexHtml) {
    return ResponseEntity.ok(indexHtml);
  }

}
