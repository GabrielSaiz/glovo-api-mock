package com.grsm.poc.glovo.api;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import org.apache.commons.lang3.RandomUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.grsm.poc.glovo.api.constants.GlovoApiConstants;
import com.grsm.poc.glovo.api.entity.Address;
import com.grsm.poc.glovo.api.entity.AddressType;
import com.grsm.poc.glovo.api.entity.Courier;
import com.grsm.poc.glovo.api.entity.EstimatePrice;
import com.grsm.poc.glovo.api.entity.Order;
import com.grsm.poc.glovo.api.entity.Position;
import com.grsm.poc.glovo.api.entity.State;
import com.grsm.poc.glovo.api.entity.WorkingAreas;
import com.grsm.poc.glovo.api.entity.mock.OrderMock;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class GlovoApiMockApplicationTests {

	// bind the above RANDOM_PORT
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    public void test_get_working_areas() throws Exception {

        ResponseEntity<WorkingAreas> response = restTemplate.getForEntity(
			new URL("http://localhost:" + port + "/b2b/working-areas").toString(), WorkingAreas.class);
        
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody());

    }
    
    @Test
	public void test_estimate_order_price_scheduled_before() throws Exception {
		Order order = OrderMock.createOrderMock();

		order.setScheduleTime(
				ZonedDateTime.of(LocalDateTime.now().minusHours(1), ZoneId.systemDefault()).toInstant().toEpochMilli());
		
		ResponseEntity<EstimatePrice> response = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/b2b/orders/estimate").toString(), order, EstimatePrice.class);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
		assertNull(((EstimatePrice)response.getBody()).getTotal());
	}

	@Test
	public void test_estimate_order_price_description_is_null() throws Exception {
		Order order = OrderMock.createOrderMock();
		order.setScheduleTime(
				ZonedDateTime.of(LocalDateTime.now().plusHours(2), ZoneId.systemDefault()).toInstant().toEpochMilli());
		order.setDescription(null);
		ResponseEntity<EstimatePrice> response = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/b2b/orders/estimate").toString(), order, EstimatePrice.class);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
		assertNull(((EstimatePrice)response.getBody()).getTotal());
	}

	@Test
	public void test_estimate_order_price_address_is_null() throws Exception {
		Order order = OrderMock.createOrderMock();
		order.setScheduleTime(null);
		order.setAddresses(null);
		ResponseEntity<EstimatePrice> response = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/b2b/orders/estimate").toString(), order, EstimatePrice.class);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCodeValue());
		assertNull(((EstimatePrice)response.getBody()).getTotal());
	}

	@Test
	public void test_estimate_order_price_address_is_empty() throws Exception {
		Order order = OrderMock.createOrderMock();
		order.setScheduleTime(null);
		order.setAddresses(new ArrayList<Address>());
		ResponseEntity<EstimatePrice> response = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/b2b/orders/estimate").toString(), order, EstimatePrice.class);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
		assertNull(((EstimatePrice)response.getBody()).getTotal());
	}

	@Test
	public void test_estimate_order_price_address_is_full() throws Exception {
		Order order = OrderMock.createOrderMock();
		order.setScheduleTime(null);
		for (int i = 0; i < 9; i++) {
			Address addressDelivery = OrderMock.createAddressWithoutType();
			addressDelivery.setType(AddressType.DELIVERY);
			order.getAddresses().add(addressDelivery);
		}
		ResponseEntity<EstimatePrice> response = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/b2b/orders/estimate").toString(), order, EstimatePrice.class);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
		assertNull(((EstimatePrice)response.getBody()).getTotal());
	}

	@Test
	public void test_estimate_order_price_address_lon_is_empty() throws Exception {
		Order order = OrderMock.createOrderMock();
		order.setScheduleTime(null);
		order.setAddresses(new ArrayList<Address>());

		Address addressDelivery = OrderMock.createAddressWithoutType();
		addressDelivery.setType(AddressType.DELIVERY);
		addressDelivery.setLon(null);
		Address addressPickup = OrderMock.createAddressWithoutType();
		addressPickup.setType(AddressType.PICKUP);

		order.getAddresses().add(addressDelivery);
		order.getAddresses().add(addressPickup);
		ResponseEntity<EstimatePrice> response = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/b2b/orders/estimate").toString(), order, EstimatePrice.class);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
		assertNull(((EstimatePrice)response.getBody()).getTotal());
	}

	@Test
	public void test_estimate_order_price_address_lat_is_empty() throws Exception {
		Order order = OrderMock.createOrderMock();
		order.setScheduleTime(null);
		order.setAddresses(new ArrayList<Address>());

		Address addressDelivery = OrderMock.createAddressWithoutType();
		addressDelivery.setType(AddressType.DELIVERY);
		addressDelivery.setLat(null);
		Address addressPickup = OrderMock.createAddressWithoutType();
		addressPickup.setType(AddressType.PICKUP);

		order.getAddresses().add(addressPickup);
		order.getAddresses().add(addressDelivery);
		ResponseEntity<EstimatePrice> response = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/b2b/orders/estimate").toString(), order, EstimatePrice.class);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
		assertNull(((EstimatePrice)response.getBody()).getTotal());
	}

	@Test
	public void test_estimate_order_price_address_position_out_of_range() throws Exception {
		Order order = OrderMock.createOrderMock();
		order.setScheduleTime(null);
		order.setAddresses(new ArrayList<Address>());

		Address addressDelivery = OrderMock.createAddressWithoutType();
		addressDelivery.setType(AddressType.DELIVERY);
		addressDelivery.setLat(GlovoApiConstants.MIN_LAT - 10);
		Address addressPickup = OrderMock.createAddressWithoutType();
		addressPickup.setType(AddressType.PICKUP);

		order.getAddresses().add(addressPickup);
		order.getAddresses().add(addressDelivery);
		ResponseEntity<EstimatePrice> response = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/b2b/orders/estimate").toString(), order, EstimatePrice.class);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
		assertNull(((EstimatePrice)response.getBody()).getTotal());
	}

	@Test
	public void test_estimate_order_price_ok() throws Exception {
		Order order = OrderMock.createOrderMock();
		order.setScheduleTime(null);
		ResponseEntity<EstimatePrice> response = restTemplate.postForEntity(
				new URL("http://localhost:" + port + "/b2b/orders/estimate").toString(), order, EstimatePrice.class);
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		assertNotNull(((EstimatePrice)response.getBody()).getTotal());
	}

	@Test
	public void test_get_single_order() throws Exception {
		ResponseEntity<Order> response = restTemplate.getForEntity(
				new URL("http://localhost:" + port + "/b2b/orders/{orderId}").toString(), Order.class, RandomUtils.nextInt());
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		assertNotNull(((Order)response.getBody()).getId());
		assertEquals(State.CANCELED, ((Order)response.getBody()).getState());
	}

	@Test
	public void test_get_order_tracking() throws Exception {
		ResponseEntity<Position> response = restTemplate.getForEntity(
				new URL("http://localhost:" + port + "/b2b/orders/{orderId}/tracking").toString(), Position.class, RandomUtils.nextInt());
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		assertNotNull(((Position)response.getBody()).getLat());
		assertNotNull(((Position)response.getBody()).getLon());		
	}

	@Test
	public void test_get_courier_contact() throws Exception {
		ResponseEntity<Courier> response = restTemplate.getForEntity(
				new URL("http://localhost:" + port + "/b2b/orders/{orderId}/courier-contact").toString(), Courier.class, RandomUtils.nextInt());
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		assertNotNull(((Courier)response.getBody()).getCourier());
	}

	@Test
	public void testGetOrdersList() throws Exception {
		given()
		.header("Content-Type", "application/json")
		.queryParams("limit", RandomUtils.nextInt(1,10), "offset", RandomUtils.nextInt(1,10))
		.when().get("/b2b/orders").then()
		.statusCode(HttpStatus.OK.value()).body(CoreMatchers.notNullValue());
	}

	@Test
	public void testCancelOrder() throws Exception {
		given()
		.header("Content-Type", "application/json")
		.when().post("/b2b/orders/" + RandomUtils.nextInt() + "/cancel").then()
		.statusCode(HttpStatus.OK.value()).body(CoreMatchers.notNullValue());
	}
	
	@Test
	public void testCreateOrder_null() throws Exception {
		
		given()
			.header("Content-Type", "application/json")
			.body("")
			.when().post("/b2b/orders/").then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body(CoreMatchers.is("Order data incomplete"));
	}
	
	@Test
	public void testCreateOrder_address_is_null() throws Exception {
		Order order = OrderMock.createOrderMock();
		order.setDescription("testCreateOrder_address_is_null");
		order.setAddresses(null);
		
		given()
			.header("Content-Type", "application/json")
			.body(order)
			.when().post("/b2b/orders/").then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body(CoreMatchers.is("Order data incomplete"));
	}
	
	@Test
	public void testCreateOrder_address_is_empty() throws Exception {
		Order order = OrderMock.createOrderMock();
		order.setDescription("testCreateOrder_address_is_empty");
		order.setAddresses(new ArrayList<Address>());
		
		given()
			.header("Content-Type", "application/json")
			.body(order)
			.when().post("/b2b/orders/").then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body(CoreMatchers.is("Order data incomplete"));
	}
	
	@Test
	public void testCreateOrder_address_is_full() throws Exception {
		Order order = OrderMock.createOrderMock();
		order.setDescription("testCreateOrder_address_is_full");
		
		for (int i = 0; i < 9; i++) {
			Address addressDelivery = OrderMock.createAddressWithoutType();
			addressDelivery.setType(AddressType.DELIVERY);
			order.getAddresses().add(addressDelivery);
		}
		
		given()
			.header("Content-Type", "application/json")
			.body(order)
			.when().post("/b2b/orders/").then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body(CoreMatchers.is("addresses size must be between 2 and 9"));
	}
	
	@Test
	public void testCreateOrder_without_pickup() throws Exception {
		Order order = OrderMock.createOrderMock();
		order.setDescription("testCreateOrder_without_pickup");
		order.setScheduleTime(null);
		order.setAddresses(new ArrayList<Address>());
		
		int numAddress = RandomUtils.nextInt(2, 9);
		for (int i = 0; i < numAddress; i++) {
			Address addressDelivery = OrderMock.createAddressWithoutType();
			addressDelivery.setType(AddressType.DELIVERY);
			order.getAddresses().add(addressDelivery);
		}
		
		given()
			.header("Content-Type", "application/json")
			.body(order)
			.when().post("/b2b/orders/").then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body(CoreMatchers.is("addresses must have at least one DELIVERY and one PICKUP"));
	}
	
	@Test
	public void testCreateOrder_without_delivery() throws Exception {
		Order order = OrderMock.createOrderMock();
		order.setDescription("testCreateOrder_without_delivery");
		order.setScheduleTime(null);
		order.setAddresses(new ArrayList<Address>());
		
		int numAddress = RandomUtils.nextInt(2, 9);
		for (int i = 0; i < numAddress; i++) {
			Address addressDelivery = OrderMock.createAddressWithoutType();
			addressDelivery.setType(AddressType.PICKUP);
			order.getAddresses().add(addressDelivery);
		}
		
		given()
			.header("Content-Type", "application/json")
			.body(order)
			.when().post("/b2b/orders/").then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body(CoreMatchers.is("addresses must have at least one DELIVERY and one PICKUP"));
	}
	
	@Test
	public void testCreateOrder_multiple_delivery_and_pickup() throws Exception {
		Order order = OrderMock.createOrderMock();
		order.setDescription("testCreateOrder_multiple_delivery_and_pickup");
		order.setScheduleTime(null);
		order.setAddresses(new ArrayList<Address>());
		
		for (int i = 0; i < 3; i++) {
			Address addressPickup = OrderMock.createAddressWithoutType();
			addressPickup.setType(AddressType.PICKUP);
			order.getAddresses().add(addressPickup);
		}
		
		for (int i = 0; i < 3; i++) {
			Address addressDelivery = OrderMock.createAddressWithoutType();
			addressDelivery.setType(AddressType.DELIVERY);
			order.getAddresses().add(addressDelivery);
		}
		
		given()
			.header("Content-Type", "application/json")
			.body(order)
			.when().post("/b2b/orders/").then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body(CoreMatchers.is("either PICKUP or DELIVERY can be multiple, but not both"));
	}
	
	
	
	@Test
	public void testCreateOrder_before() throws Exception {
		Order order = OrderMock.createOrderMock();
		order.setDescription("testCreateOrder_before");

		order.setScheduleTime(
				ZonedDateTime.of(LocalDateTime.now().minusHours(1), ZoneId.systemDefault()).toInstant().toEpochMilli());
		
		given()
			.header("Content-Type", "application/json")
			.body(order)
			.when().post("/b2b/orders/").then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body(CoreMatchers.is("scheduleTime must be in the future"));
	}
	
	@Test
	public void testCreateOrder_one_way() throws Exception {
		Order order = OrderMock.createOrderMock();
		order.setDescription("testCreateOrder_one_way");
		order.setScheduleTime(null);
		
		order.setAddresses(new ArrayList<Address>());
		
		Address addressDelivery = OrderMock.createAddressWithoutType();
		addressDelivery.setType(AddressType.DELIVERY);
		order.getAddresses().add(addressDelivery);
		Address addressPickup = OrderMock.createAddressWithoutType();
		addressPickup.setType(AddressType.PICKUP);
		order.getAddresses().add(addressPickup);
		
		given()
			.header("Content-Type", "application/json")
			.body(order).when().post("/b2b/orders/")
			.then().statusCode(HttpStatus.OK.value())
			.body(CoreMatchers.notNullValue());
		
	}
	
	@Test
	public void testCreateOrder_one_way_scheduled() throws Exception {
		Order order = OrderMock.createOrderMock();
		order.setDescription("testCreateOrder_one_way_scheduled");
		order.setScheduleTime(
				ZonedDateTime.of(LocalDateTime.now().plusHours(1), ZoneId.systemDefault()).toInstant().toEpochMilli());
		
		order.setAddresses(new ArrayList<Address>());
		
		Address addressDelivery = OrderMock.createAddressWithoutType();
		addressDelivery.setType(AddressType.DELIVERY);
		order.getAddresses().add(addressDelivery);
		Address addressPickup = OrderMock.createAddressWithoutType();
		addressPickup.setType(AddressType.PICKUP);
		order.getAddresses().add(addressPickup);
		
		given()
			.header("Content-Type", "application/json")
			.body(order).when().post("/b2b/orders/")
			.then().statusCode(HttpStatus.OK.value())
			.body(CoreMatchers.notNullValue());
		
		given()
			.header("Content-Type", "application/json")
			.body(order).when().post("/b2b/orders/")
			.then().statusCode(HttpStatus.OK.value())
			.body(CoreMatchers.notNullValue());
	}
	
	@Test
	public void testCreateOrder_multiple_pickup() throws Exception {
		Order order = OrderMock.createOrderMock();		
		order.setScheduleTime(null);
		order.setDescription("testCreateOrder_multiple_pickup");
		order.setAddresses(new ArrayList<Address>());
		
		Address addressDelivery = OrderMock.createAddressWithoutType();
		addressDelivery.setType(AddressType.DELIVERY);
		order.getAddresses().add(addressDelivery);
		Address addressPickup = OrderMock.createAddressWithoutType();
		addressPickup.setType(AddressType.PICKUP);
		order.getAddresses().add(addressPickup);
		Address addressPickup2 = OrderMock.createAddressWithoutType();
		addressPickup2.setType(AddressType.PICKUP);
		order.getAddresses().add(addressPickup2);
		
		given()
			.header("Content-Type", "application/json")
			.body(order).when().post("/b2b/orders/")
			.then().statusCode(HttpStatus.OK.value())
			.body(CoreMatchers.notNullValue());
	}
	
	@Test
	public void testCreateOrder_multiple_pickup_scheduled() throws Exception {
		Order order = OrderMock.createOrderMock();		
		order.setDescription("testCreateOrder_multiple_pickup_scheduled");
		order.setScheduleTime(
				ZonedDateTime.of(LocalDateTime.now().plusHours(1), ZoneId.systemDefault()).toInstant().toEpochMilli());
		order.setAddresses(new ArrayList<Address>());
		
		Address addressDelivery = OrderMock.createAddressWithoutType();
		addressDelivery.setType(AddressType.DELIVERY);
		order.getAddresses().add(addressDelivery);
		Address addressPickup = OrderMock.createAddressWithoutType();
		addressPickup.setType(AddressType.PICKUP);
		order.getAddresses().add(addressPickup);
		Address addressPickup2 = OrderMock.createAddressWithoutType();
		addressPickup2.setType(AddressType.PICKUP);
		order.getAddresses().add(addressPickup2);
		
		given()
			.header("Content-Type", "application/json")
			.body(order).when().post("/b2b/orders/")
			.then().statusCode(HttpStatus.OK.value())
			.body(CoreMatchers.notNullValue());
	}
	
	@Test
	public void testCreateOrder_multiple_drop_off() throws Exception {
		Order order = OrderMock.createOrderMock();	
		order.setDescription("testCreateOrder_multiple_drop_off");
		order.setScheduleTime(null);
		
		order.setAddresses(new ArrayList<Address>());
		
		Address addressPickup = OrderMock.createAddressWithoutType();
		addressPickup.setType(AddressType.PICKUP);
		order.getAddresses().add(addressPickup);
		
		for (int i = 0; i < 3; i++) {
			Address addressDelivery = OrderMock.createAddressWithoutType();
			addressDelivery.setType(AddressType.DELIVERY);
			order.getAddresses().add(addressDelivery);
		}
		
		given()
			.header("Content-Type", "application/json")
			.body(order).when().post("/b2b/orders/")
			.then().statusCode(HttpStatus.OK.value())
			.body(CoreMatchers.notNullValue());
	}
	
	@Test
	public void testCreateOrder_multiple_drop_off_scheduled() throws Exception {
		Order order = OrderMock.createOrderMock();	
		order.setDescription("testCreateOrder_multiple_drop_off_scheduled");
		order.setScheduleTime(
				ZonedDateTime.of(LocalDateTime.now().plusHours(1), ZoneId.systemDefault()).toInstant().toEpochMilli());
		
		order.setAddresses(new ArrayList<Address>());
		
		Address addressPickup = OrderMock.createAddressWithoutType();
		addressPickup.setType(AddressType.PICKUP);
		order.getAddresses().add(addressPickup);
		
		for (int i = 0; i < 3; i++) {
			Address addressDelivery = OrderMock.createAddressWithoutType();
			addressDelivery.setType(AddressType.DELIVERY);
			order.getAddresses().add(addressDelivery);
		}
		
		given()
			.header("Content-Type", "application/json")
			.body(order).when().post("/b2b/orders/")
			.then().statusCode(HttpStatus.OK.value())
			.body(CoreMatchers.notNullValue());
	}
	
	
	@Test
	public void testCreateOrder_multiple_drop_off_two_delivery() throws Exception {
		Order order = OrderMock.createOrderMock();	
		order.setDescription("testCreateOrder_multiple_drop_off_two_delivery");
		order.setScheduleTime(null);
		
		order.setAddresses(new ArrayList<Address>());
		
		Address addressPickup = OrderMock.createAddressWithoutType();
		addressPickup.setType(AddressType.PICKUP);
		order.getAddresses().add(addressPickup);
		
		for (int i = 0; i < 2; i++) {
			Address addressDelivery = OrderMock.createAddressWithoutType();
			addressDelivery.setType(AddressType.DELIVERY);
			order.getAddresses().add(addressDelivery);
		}
		
		given()
			.header("Content-Type", "application/json")
			.body(order).when().post("/b2b/orders/")
			.then().statusCode(HttpStatus.OK.value())
			.body(CoreMatchers.notNullValue());
	}
	
	@Test
	public void testCreateOrder_multiple_drop_off_two_delivery_scheduled() throws Exception {
		Order order = OrderMock.createOrderMock();	
		order.setDescription("testCreateOrder_multiple_drop_off_two_delivery_scheduled");
		order.setScheduleTime(
				ZonedDateTime.of(LocalDateTime.now().plusHours(1), ZoneId.systemDefault()).toInstant().toEpochMilli());
		
		order.setAddresses(new ArrayList<Address>());
		
		Address addressPickup = OrderMock.createAddressWithoutType();
		addressPickup.setType(AddressType.PICKUP);
		order.getAddresses().add(addressPickup);
		
		for (int i = 0; i < 2; i++) {
			Address addressDelivery = OrderMock.createAddressWithoutType();
			addressDelivery.setType(AddressType.DELIVERY);
			order.getAddresses().add(addressDelivery);
		}
		
		given()
			.header("Content-Type", "application/json")
			.body(order).when().post("/b2b/orders/")
			.then().statusCode(HttpStatus.OK.value())
			.body(CoreMatchers.notNullValue());
	}
	
	@Test
	public void testCreateOrder_return() throws Exception {
		Order order = OrderMock.createOrderMock();	
		order.setDescription("testCreateOrder_return");
		order.setScheduleTime(null);
		
		order.setAddresses(new ArrayList<Address>());
		
		Address addressDelivery = OrderMock.createAddressWithoutType();
		addressDelivery.setType(AddressType.DELIVERY);
		order.getAddresses().add(addressDelivery);
		Address addressPickup = OrderMock.createAddressWithoutType();
		addressPickup.setType(AddressType.PICKUP);
		order.getAddresses().add(addressPickup);
		
		Address addressDelivery2 = new Address();
		addressDelivery2.setContactPerson(addressPickup.getContactPerson());
		addressDelivery2.setDetails(addressPickup.getDetails());
		addressDelivery2.setInstructions(addressPickup.getInstructions());
		addressDelivery2.setLabel(addressPickup.getLabel());
		addressDelivery2.setLat(addressPickup.getLat());
		addressDelivery2.setLon(addressPickup.getLon());
		addressDelivery2.setType(AddressType.DELIVERY);
		order.getAddresses().add(addressDelivery2);
		
		given()
			.header("Content-Type", "application/json")
			.body(order).when().post("/b2b/orders/")
			.then().statusCode(HttpStatus.OK.value())
			.body(CoreMatchers.notNullValue());
	}
	
	@Test
	public void testCreateOrder_return_scheduled() throws Exception {
		Order order = OrderMock.createOrderMock();	
		order.setDescription("testCreateOrder_return_scheduled");
		order.setScheduleTime(
				ZonedDateTime.of(LocalDateTime.now().plusHours(1), ZoneId.systemDefault()).toInstant().toEpochMilli());
		
		
		order.setAddresses(new ArrayList<Address>());
		
		Address addressDelivery = OrderMock.createAddressWithoutType();
		addressDelivery.setType(AddressType.DELIVERY);
		order.getAddresses().add(addressDelivery);
		Address addressPickup = OrderMock.createAddressWithoutType();
		addressPickup.setType(AddressType.PICKUP);
		order.getAddresses().add(addressPickup);
		
		Address addressDelivery2 = new Address();
		addressDelivery2.setContactPerson(addressPickup.getContactPerson());
		addressDelivery2.setDetails(addressPickup.getDetails());
		addressDelivery2.setInstructions(addressPickup.getInstructions());
		addressDelivery2.setLabel(addressPickup.getLabel());
		addressDelivery2.setLat(addressPickup.getLat());
		addressDelivery2.setLon(addressPickup.getLon());
		addressDelivery2.setType(AddressType.DELIVERY);
		order.getAddresses().add(addressDelivery2);
		
		given()
			.header("Content-Type", "application/json")
			.body(order).when().post("/b2b/orders/")
			.then().statusCode(HttpStatus.OK.value())
			.body(CoreMatchers.notNullValue());
	}

    
}
