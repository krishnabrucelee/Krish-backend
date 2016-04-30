package ck.panda.util;

import java.util.Comparator;

public class Instance implements Comparable<Instance> {
	private Long id;
	private String ipaddress;

	/**
	 * Comparator implementation to Sort Order object based on Ipaddress.
	 */
	public static class OrderByIpAddressByASC implements Comparator<Instance> {

		@Override
		public int compare(Instance o1, Instance o2) {
			 String[] ips1 = o1.ipaddress.split("\\.");
	            String updatedIp1 = String.format("%3s.%3s.%3s.%3s",
	                                                  ips1[0],ips1[1],ips1[2],ips1[3]);
	            String[] ips2 = o2.ipaddress.split("\\.");
	            String updatedIp2 = String.format("%3s.%3s.%3s.%3s",
	                                                  ips2[0],ips2[1],ips2[2],ips2[3]);
	            return updatedIp1.compareTo(updatedIp2);
		}
	}

	@Override
	public int compareTo(Instance o) {
		return this.id > o.id ? 1 : (this.id < o.id ? -1 : 0);
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the ipaddress
	 */
	public String getIpaddress() {
		return ipaddress;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @param ipaddress the ipaddress to set
	 */
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
}
