package io.wttech.habit.server.environment.configuration.request;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
public class ProxyServerConfiguration {

  @JsonSetter(nulls = Nulls.SKIP)
  private String image;
  @JsonSetter(nulls = Nulls.SKIP)
  private String containerHostname;
  @JsonMerge
  @JsonSetter(nulls = Nulls.SKIP)
  private List<String> capabilities;
  @JsonSetter(nulls = Nulls.SKIP)
  private Map<String, String> tmpfs;
  @JsonMerge
  @JsonSetter(nulls = Nulls.SKIP)
  private List<Mount> mounts = new ArrayList<>();
  @JsonMerge
  @JsonSetter(nulls = Nulls.SKIP)
  private Map<String, String> volumes = new HashMap<>();

  ProxyServerConfiguration() {
  }

  ProxyServerConfiguration(String image,
      String containerHostname,
      List<String> capabilities,
      Map<String, String> tmpfs,
      List<Mount> mounts,
      Map<String, String> volumes) {
    this.image = image;
    this.containerHostname = containerHostname;
    this.capabilities = capabilities;
    this.tmpfs = tmpfs;
    this.mounts = mounts;
    this.volumes = volumes;
  }

  public String getImage() {
    return image;
  }

  public String getContainerHostname() {
    return containerHostname;
  }

  public boolean isHostnamePresent() {
    return containerHostname != null;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public void setContainerHostname(String containerHostname) {
    this.containerHostname = containerHostname;
  }

  public List<String> getCapabilities() {
    return capabilities;
  }

  public void setCapabilities(List<String> capabilities) {
    this.capabilities = capabilities;
  }

  public Map<String, String> getTmpfs() {
    return tmpfs;
  }

  public void setTmpfs(Map<String, String> tmpfs) {
    this.tmpfs = tmpfs;
  }

  public List<Mount> getMounts() {
    return mounts;
  }

  public void setMounts(List<Mount> mounts) {
    this.mounts = mounts;
  }

  public Map<String, String> getVolumes() {
    return volumes;
  }

  public void setVolumes(Map<String, String> volumes) {
    this.volumes = volumes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProxyServerConfiguration that = (ProxyServerConfiguration) o;
    return Objects.equals(image, that.image) &&
        Objects.equals(containerHostname, that.containerHostname) &&
        Objects.equals(capabilities, that.capabilities) &&
        Objects.equals(tmpfs, that.tmpfs) &&
        Objects.equals(mounts, that.mounts) &&
        Objects.equals(volumes, that.volumes);
  }

  @Override
  public int hashCode() {
    return Objects
        .hash(image, containerHostname, capabilities,
            tmpfs, mounts, volumes);
  }

}
