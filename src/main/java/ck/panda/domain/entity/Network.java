package ck.panda.domain.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *  Network can be accessed by virtual machines that belong to many different accounts.
 *
 */
@Entity
@Table(name = "ck_network")
@SuppressWarnings("serial")
public class Network implements Serializable {

     /** Id of the network. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Name of the network. */
    @Column(name = "name", nullable = false)
    private String name;

    /** whether this network can be used for deployment. */
    @Column(name = "can_use_for_deploy")
    private Boolean canUseForDeploy;

    /** cidr value.*/
    @Column(name = "cidr")
    private String cidr;

    /** Unique ID from Cloud Stack. */
    @Column(name = "uuid")
    private String uuid;

    /** whether to display network or not. */
    @Column(name = "display_network")
    private Boolean displayNetwork;

    /** display text for network. */
    @Column(name = "display_text")
    private String displayText;

    /** Id of the domain. */
    @Column(name = "domain_id")
    private String domainId;

    /** whether to defualt or not. */
    @Column(name = "is_default")
    private Boolean isDefault;

    /** whether issystem for network. */
    @Column(name = "is_system")
    private Boolean isSystem;

    /** Id of the network offering. */
    @Column(name = "network_offering_id")
    private String networkOfferingId;

    /** state of the network. */
    @Column(name = "state")
    private String state;

    /** traffic type of the network.*/
    @Column(name = "traffic_type")
    private String trafficType;

    /** Id of the zone. */
    @Column(name = "zone_id")
    private String zoneId;

    /** whether issystem for network. */
    @Column(name = "list_all", columnDefinition = "tinyint default 1")
    private Boolean listAll;

    /** boolean is recursive. */
    @Column(name = "is_recursive", columnDefinition = "tinyint default 1")
    private Boolean isRecursive;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the canUseForDeploy
     */
    public Boolean getCanUseForDeploy() {
        return canUseForDeploy;
    }

    /**
     * @param canUseForDeploy the canUseForDeploy to set
     */
    public void setCanUseForDeploy(Boolean canUseForDeploy) {
        this.canUseForDeploy = canUseForDeploy;
    }

    /**
     * @return the cidr
     */
    public String getCidr() {
        return cidr;
    }

    /**
     * @param cidr the cidr to set
     */
    public void setCidr(String cidr) {
        this.cidr = cidr;
    }

    /**
     * @return the displayNetwork
     */
    public Boolean getDisplayNetwork() {
        return displayNetwork;
    }

    /**
     * @param displayNetwork the displayNetwork to set
     */
    public void setDisplayNetwork(Boolean displayNetwork) {
        this.displayNetwork = displayNetwork;
    }

    /**
     * @return the displayText
     */
    public String getDisplayText() {
        return displayText;
    }

    /**
     * @param displayText the displayText to set
     */
    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    /**
     * @return the domainId
     */
    public String getDomainId() {
        return domainId;
    }

    /**
     * @param domainId the domainId to set
     */
    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    /**
     * @return the isDefault
     */
    public Boolean getIsDefault() {
        return isDefault;
    }

    /**
     * @param isDefault the isDefault to set
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * @return the isSystem
     */
    public Boolean getIsSystem() {
        return isSystem;
    }

    /**
     * @param isSystem the isSystem to set
     */
    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }

    /**
     * @return the networkOfferingId
     */
    public String getNetworkOfferingId() {
        return networkOfferingId;
    }

    /**
     * @param networkOfferingId the networkOfferingId to set
     */
    public void setNetworkOfferingId(String networkOfferingId) {
        this.networkOfferingId = networkOfferingId;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the trafficType
     */
    public String getTrafficType() {
        return trafficType;
    }

    /**
     * @param trafficType the trafficType to set
     */
    public void setTrafficType(String trafficType) {
        this.trafficType = trafficType;
    }

    /**
     * @return the zoneId
     */
    public String getZoneId() {
        return zoneId;
    }

    /**
     * @param zoneId the zoneId to set
     */
    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    /**
     * @return the listAll
     */
    public Boolean getListAll() {
        return listAll;
    }

    /**
     * @param listAll the listAll to set
     */
    public void setListAll(Boolean listAll) {
        this.listAll = listAll;
    }


    /**
     * @return the isRecursive
     */
    public Boolean getIsRecursive() {
        return isRecursive;
    }

    /**
     * @param isRecursive the isRecursive to set
     */
    public void setIsRecursive(Boolean isRecursive) {
        this.isRecursive = isRecursive;
    }

    /**
     * Convert JSONObject to network entity.
     *
     * @param object json object
     * @return domain entity object.
     * @throws JSONException handles json exception.
     */
    public static Network convert(JSONObject object) throws JSONException {
        Network network = new Network();
        network.uuid = object.get("id").toString();
        network.name = object.get("name").toString();
        network.networkOfferingId = object.get("networkofferingid").toString();
        network.cidr = object.get("cidr").toString();
        network.domainId = object.get("domainid").toString();
        network.zoneId = object.get("zoneid").toString();
        network.state = object.get("state").toString();
        network.isSystem = (Boolean) object.get("issystem");
        return network;
    }

    /**
     * Mapping entity object into list.
     *
     * @param networkList list of domains.
     * @return network map
     */
    public static Map<String, Network> convert(List<Network> networkList) {
        Map<String, Network> networkMap = new HashMap<String, Network>();

        for (Network network : networkList) {
            networkMap.put(network.getUuid(), network);
        }
        return networkMap;
    }
  }
