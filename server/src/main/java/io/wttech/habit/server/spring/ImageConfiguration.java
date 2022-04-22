package io.wttech.habit.server.spring;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties("habit.images")
@AllArgsConstructor
public class ImageConfiguration {

  private final CommonImagesConfiguration common;
  private final ImageItemConfiguration requestPersister;
  private final ImageItemConfiguration requestGraphServer;
  private final ImageItemConfiguration mockServer;
  private final ImageItemConfiguration environmentFrontProxy;

  public String getRequestPersisterImageId() {
    return combineCommonAndOverrides(requestPersister);
  }

  public String getRequestGraphServerImageId() {
    return combineCommonAndOverrides(requestGraphServer);
  }

  public String getMockServerImageId() {
    return combineCommonAndOverrides(mockServer);
  }

  public String getEnvironmentFrontProxyImageId() {
    return combineCommonAndOverrides(environmentFrontProxy);
  }

  private String combineCommonAndOverrides(ImageItemConfiguration imageConfiguration) {
    String registry = isOverridePresent(imageConfiguration.getRegistry())
        ? imageConfiguration.getRegistry()
        : common.getRegistry();
    String folder = isOverridePresent(imageConfiguration.getFolder())
        ? imageConfiguration.getFolder()
        : common.getFolder();
    String tag = isOverridePresent(imageConfiguration.getTag())
        ? imageConfiguration.getTag()
        : common.getTag();
    String imageName = imageConfiguration.getImage();
    return constructFullImageId(registry, folder, imageName, tag);
  }

  private boolean isOverridePresent(String override) {
    return override != null && !override.isEmpty();
  }

  private String constructFullImageId(String registry, String folder, String imageName, String tag) {
    StringBuilder stringBuilder = new StringBuilder();
    if (!registry.isEmpty()) {
      stringBuilder.append(registry).append("/");
    }
    stringBuilder.append(folder);
    if (!folder.endsWith("/")) {
      stringBuilder.append("/");
    }
    stringBuilder.append(imageName).append(":").append(tag);
    return stringBuilder.toString();
  }

  @AllArgsConstructor
  @Getter
  public static class CommonImagesConfiguration {

    private final String registry;
    private final String folder;
    private final String tag;

  }

  @AllArgsConstructor
  @Getter
  public static class ImageItemConfiguration {

    private final String registry;
    private final String folder;
    private final String image;
    private final String tag;

  }

}
