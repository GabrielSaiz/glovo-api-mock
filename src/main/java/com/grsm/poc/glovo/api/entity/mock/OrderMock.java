package com.grsm.poc.glovo.api.entity.mock;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import com.grsm.poc.glovo.api.constants.GlovoApiConstants;
import com.grsm.poc.glovo.api.entity.Address;
import com.grsm.poc.glovo.api.entity.AddressType;
import com.grsm.poc.glovo.api.entity.Order;
import com.grsm.poc.glovo.api.entity.Reference;
import com.grsm.poc.glovo.api.entity.State;
import com.thedeanda.lorem.LoremIpsum;

public class OrderMock {
	
	public static Order createOrderMock() {
		Order order = new Order();
		order.setAddresses(createAddresses());
		order.setId(RandomStringUtils.randomNumeric(10));
		order.setCode(RandomStringUtils.randomAlphanumeric(10).toUpperCase());
		order.setDescription(LoremIpsum.getInstance().getWords(RandomUtils.nextInt(2, 5)));
		order.setScheduleTime(ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant().toEpochMilli());

		switch (RandomUtils.nextInt(0, 4)) {
		case 0:
			order.setState(State.ACTIVE);
			break;
		case 1:
			order.setState(State.CANCELED);
			break;
		case 2:
			order.setState(State.DELIVERED);
			break;
		default:
			order.setState(State.SCHEDULED);
			break;
		}

		Reference reference = new Reference();
		reference.setId("GRSM-" + RandomStringUtils.randomAlphanumeric(10).toUpperCase());
		order.setReference(reference);

		return order;
	}

	public static List<Address> createAddresses() {
		List<Address> listAddresses = new ArrayList<Address>();
		int numAddress = RandomUtils.nextInt(2, 9);
		int numPickUp = 0;
		int numDelivery = 0;
		for (int i = 0; i <= numAddress; i++) {
			Address address = createAddressWithoutType();
			
			if (numDelivery == 0) {
				address.setType(AddressType.DELIVERY);
				numDelivery++;
			} else if (numPickUp == 0) {
				address.setType(AddressType.PICKUP);
				numPickUp++;				
			} else if ((numDelivery == 1) && (numPickUp == 1)) {
				AddressType type = (RandomUtils.nextBoolean()) ? AddressType.DELIVERY : AddressType.PICKUP;
				address.setType(type);
				if (AddressType.DELIVERY.equals(type)) {
					numDelivery++;
				} else {
					numPickUp++;
				}
			} else if (numDelivery > 1) {
				address.setType(AddressType.DELIVERY);
				numDelivery++;
			} else if (numPickUp > 1) {
				address.setType(AddressType.PICKUP);
				numPickUp++;
			}
			
			listAddresses.add(address);
		}
		return listAddresses;
	}

	public static Address createAddressWithoutType() {
		Address address = new Address();
		address.setLat(RandomUtils.nextDouble(GlovoApiConstants.MIN_LAT, GlovoApiConstants.MAX_LAT));
		Double longitud = RandomUtils.nextDouble(0, GlovoApiConstants.MIN_LONG*(-1));
		if (longitud > GlovoApiConstants.MAX_LONG) {
			longitud = longitud * (-1);
		} else {
			longitud = longitud * ((RandomUtils.nextBoolean())?1:-1);
		}
		address.setLon(longitud);

		address.setLabel(LoremIpsum.getInstance().getWords(RandomUtils.nextInt(2, 5)));
		address.setDetails(LoremIpsum.getInstance().getWords(RandomUtils.nextInt(4, 10)));
		address.setContactPhone(RandomStringUtils.randomNumeric(9));
		address.setContactPerson("Nirtam Zias Leirbag");
		address.setInstructions(LoremIpsum.getInstance().getWords(RandomUtils.nextInt(3, 12)));
		return address;
	}

}
