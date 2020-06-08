package com.grsm.poc.glovo.api.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.grsm.poc.glovo.api.constants.GlovoApiConstants;
import com.grsm.poc.glovo.api.entity.Address;
import com.grsm.poc.glovo.api.entity.AddressType;
import com.grsm.poc.glovo.api.entity.Courier;
import com.grsm.poc.glovo.api.entity.EstimatePrice;
import com.grsm.poc.glovo.api.entity.Order;
import com.grsm.poc.glovo.api.entity.Orders;
import com.grsm.poc.glovo.api.entity.Position;
import com.grsm.poc.glovo.api.entity.State;
import com.grsm.poc.glovo.api.entity.Total;
import com.grsm.poc.glovo.api.entity.mock.OrderMock;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/b2b/orders")
public class OrdersController {

    /**
     * Provide a price estimation for an order.
     *
     * The response amount will always be in the minor of the currency (e.g. cents for EUR).
     *
     * Validations:
     * - schedureTime: tiene que ser nulo o del futuro
     * - description: no puede ser nulo
     * - Addresses:
     *      - Mayor de 2 y menor de 9
     *      - Tiene que haber al menos una pickup y una delivery
     *      - Puede haber más de un pickup
     *      - Puede haber más de un delivery
     *      - No puede haber más de un pickup y más de un delivery a la vez
     *      - Las latitudes tienen que encajar en alguna de las areas de trabajo (servicio de working-areas)
     *
     * Request
     *
     * POST /b2b/orders/estimate
     * {
     *   "scheduleTime": 1568879245000,
     *   "description": "A 30cm by 30cm box",
     *   "addresses": [
     *     {
     *       "type": "PICKUP",
     *       "lat": 0.1,
     *       "lon": 0.3,
     *       "label": "Calle la X, 29"
     *     },
     *     {
     *       "type": "DELIVERY",
     *       "lat": 0.1,
     *       "lon": 0.3,
     *       "label": "Calle la X, 30"
     *     }
     *   ]
     * }
     * Response returning a 5.90 EUR price
     *
     * {
     *   "total": {
     *     "amount": 590,
     *     "currency": "EUR"
     *   }
     * }
     *
     *  @param order
     * @return
     */
    @ResponseBody
    @PostMapping("/estimate")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @Operation(summary = "Provide a price estimation for an order.", security = @SecurityRequirement(name = "basicAuth"))
    public EstimatePrice estimateOrderPrice(@RequestBody Order order) {

        if (order.getScheduleTime() != null) {
            LocalDateTime triggerTime =
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(order.getScheduleTime()),
                            TimeZone.getDefault().toZoneId());

            if (triggerTime.isBefore(LocalDateTime.now(TimeZone.getDefault().toZoneId()))) {
            	
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "scheduleTime must be in the future");
            }
        }

        if (StringUtils.isEmpty(order.getDescription())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "description may not be empty, scheduleTime must be in the future");
        }

        if (order.getAddresses() == null ){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "HV000028: Unexpected exception during isValid call.");
        }

        if (order.getAddresses().size() < 2 || order.getAddresses().size() > 9) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "addresses size must be between 2 and 9");
        }
        long numDelivery = order.getAddresses().stream()
                .filter(t -> AddressType.DELIVERY.equals(t.getType()))
                .count();

        long numPickup = order.getAddresses().stream()
                .filter(t -> AddressType.PICKUP.equals(t.getType()))
                .count();

        if ((numDelivery == 0) || (numPickup == 0) || ((numDelivery > 1) && (numPickup > 1))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "addresses must have at least one DELIVERY and one PICKUP, and either PICKUP or DELIVERY can be multiple, but not both");
        }

        order.getAddresses().stream().forEach(t -> {
            if (t.getLon() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "lon may not be null");
            }
            if (t.getLat() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "lat may not be null");
            }
            if ((GlovoApiConstants.MIN_LAT > t.getLat())
                    || (GlovoApiConstants.MAX_LAT < t.getLat())
                    || (GlovoApiConstants.MIN_LONG > t.getLon())
                    || (GlovoApiConstants.MAX_LONG < t.getLon() )) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "addresses must have at least one DELIVERY and one PICKUP, and either PICKUP or DELIVERY can be multiple, but not both");
            }
        });

        Total total = new Total();
        total.setCurrency("EUR");
        total.setAmount(699 + 100*(order.getAddresses().size()-2));

        EstimatePrice estimatePrice = new EstimatePrice();
        estimatePrice.setTotal(total);

        return estimatePrice;
    }

    /**
     * Create an order:
     * - Create a One Way order: Create an order. If you want the order to be scheduled, provide a scheduleTime in milliseconds (see Order). Otherwise, it'll be scheduled for immediate delivery.
     * - Create a Return order: Create a return order by adding a second DELIVERY address that is identical to the PICKUP (label, lat, lon must be the same).
     * - Create a Multiple Pick-Up order: Create a multiple pick-up order by creating up to eight PICKUP addresses and one DELIVERY address.
     * - Create Multiple Drop-Off Order: Create a multiple drop-off order by adding one PICKUP and up to eight DELIVERY addresses.
     *
     */
    @ResponseBody
    @PostMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Create an order", security = @SecurityRequirement(name = "basicAuth"))
    public Order createOrder(@RequestBody Order order) {
        if ((order == null) || (order.getAddresses() == null) || (order.getAddresses().size() < 2)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order data incomplete");
            
        } else if (order.getAddresses().size() > 9) {
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "addresses size must be between 2 and 9");
    	} else  if (order.getScheduleTime() != null) {
    		LocalDateTime triggerTime =
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(order.getScheduleTime()),
                            TimeZone.getDefault().toZoneId());

            if (triggerTime.isBefore(LocalDateTime.now(TimeZone.getDefault().toZoneId()))) {
            	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "scheduleTime must be in the future");
            	
            }
    	}
        

		long numDelivery = order.getAddresses().stream().filter(t -> AddressType.DELIVERY.equals(t.getType())).count();

		long numPickup = order.getAddresses().stream().filter(t -> AddressType.PICKUP.equals(t.getType())).count();
		
		if ((numDelivery == 0) || (numPickup == 0)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"addresses must have at least one DELIVERY and one PICKUP");
		}

		if ((numDelivery > 1) && (numPickup > 1)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"either PICKUP or DELIVERY can be multiple, but not both");
		}
		
		order.getAddresses().stream().forEach(t -> {
			if (StringUtils.isEmpty(t.getLabel())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"label may not be empty");
			}
		});


		if (numDelivery == 1) {
			if (numPickup == 1) {
				// is a Create One Way Order
				order = createOneWayOrder(order);
			} else {
				// is a Create Multiple Pick-Up Order
				order = createMultiplePickUpOrder(order);
			}
		} else if (numDelivery == 2) {
			Comparator<Address> addressComparator = new Comparator<Address>() {

				@Override
				public int compare(Address a1, Address a2) {
					int result = (a1.getLat().equals(a2.getLat()) && a1.getLon().equals(a2.getLon())) ? 0 : 1;

					return result;
				}
			};

			Address pickUp = order.getAddresses().stream().filter(a -> AddressType.PICKUP.equals(a.getType()))
					.findFirst().get();
			Address returnAddress = order.getAddresses().stream().filter(
					ra -> AddressType.DELIVERY.equals(ra.getType()) && addressComparator.compare(ra, pickUp) == 0)
					.findFirst().orElse(null);

			if (returnAddress != null) {
				order = createReturnOrder(order);
			} else {

				// is a Create Multiple Drop-Off Order
				order = createMultipleDropOffOrder(order);
			}
		} else {
			// is a Create Multiple Drop-Off Order
			order = createMultipleDropOffOrder(order);
		}				

        return order;
    }

    /**
     * Retrieve information about a single order.
     *
     * Request
     *
     * GET /b2b/orders/:id
     * Response
     *
     * {
     *   "id": 123456789,
     *   "code": "AA12AA1BB",
     *   "state": "SCHEDULED",
     *   "scheduleTime": 1568879245000,
     *   "description": "A 30cm by 30cm box",
     *   "reference": {
     *     "id": "your internal reference"
     *   },
     *   "addresses": [
     *     {
     *       "type": "PICKUP",
     *       "lat": 0.1,
     *       "lon": 0.3,
     *       "label": "Calle la X, 29",
     *       "details": "2nd Floor",
     *       "contactPhone": "+34622334455",
     *       "contactPerson": "Sam Romero",
     *       "instructions": "Use the stairs to access this address"
     *     },
     *     {
     *       "type": "DELIVERY",
     *       "lat": 0.2,
     *       "lon": 0.4,
     *       "label": "Calle la Y, 30",
     *       "details": "Blue button of the intercom",
     *       "contactPhone": "+34622334455",
     *       "contactPerson": "Alex Smith",
     *       "instructions": "If recipient is unavailable leave next door"
     *     }
     *   ]
     * }
     *
     * @param orderId
     * @return
     */
    @ResponseBody
    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get Single Order", security = @SecurityRequirement(name = "basicAuth"))
    public Order getSingleOrder(@PathVariable String orderId) {
		Order order = OrderMock.createOrderMock();

		order.setId(orderId);
		order.setCode("SO_" + order.getCode() + "_NOW");

		return order;
    }

    /**
     * Return the position (latitude, longitude) of the courier.
     *
     * Request
     *
     * GET /b2b/orders/:id/tracking
     * Response
     *
     * {
     *   "lat": 0.1234,
     *   "lon": 0.1234
     * }
     *
     * @param orderId
     * @return
     */
    @ResponseBody
    @GetMapping("/{orderId}/tracking")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return the position (latitude, longitude) of the courier.", security = @SecurityRequirement(name = "basicAuth"))
    public Position getOrderTracking(@PathVariable String orderId) {
        Position position = new Position();
        position.setLat(40.4170769);
        position.setLon(-3.7033539);

        return position;
    }

    /**
     * Name and contact phone of the courier if the order is active. Error if the order is not active.
     *
     * Request
     *
     * GET /b2b/orders/:id/courier-contact
     * Response
     *
     * {
     *   "courier": "Alfonso",
     *   "phone": "+34666123123"
     * }
     *
     * @param orderId
     * @return
     */
    @ResponseBody
    @GetMapping("/{orderId}/courier-contact")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Name and contact phone of the courier if the order is active. Error if the order is not active.", security = @SecurityRequirement(name = "basicAuth"))
    public Courier getCourierContact(@PathVariable String orderId) {
        Courier courier = new Courier();
        courier.setCourier("Niarudni Leugim");
        courier.setPhone("+34666123123");

        return courier;
    }

    /**
     * Retrieve a list of orders up to the limit count starting from offset in the reverse chronological order.
     *
     * Request
     *
     * GET /b2b/orders?limit=<number>&offset=<number>
     *
     * Response
     *
     * [
     *   // List of orders
     * ]
     *
     * @param limit
     * @param offset
     * @return
     */
    @ResponseBody
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retrieve a list of orders up to the limit count starting from offset in the reverse chronological order.", security = @SecurityRequirement(name = "basicAuth"))
    public Orders getOrdersList(@RequestParam Integer limit, @RequestParam Integer offset) {

		List<Order> lisOrders = new ArrayList<Order>();
		for (int i = 0; i < limit; i++) {
			lisOrders.add(OrderMock.createOrderMock());
		}

		Orders orders = new Orders();
		orders.setOrders(lisOrders);		

        return orders;
    }

    /**
     * Cancel a scheduled order. Active orders cannot be canceled.
     *
     * Request
     *
     * POST /b2b/orders/:id/cancel
     * {}
     * Response
     *
     * {
     *   "id": 123456789,
     *   "code": "AA12AA1BB",
     *   "state": "CANCELED",
     *   "scheduleTime": 1568879245000,
     *   "description": "A 30cm by 30cm box",
     *   "reference": {
     *     "id": "your internal reference"
     *   },
     *   "addresses": [
     *     {
     *       "type": "PICKUP",
     *       "lat": 0.1,
     *       "lon": 0.3,
     *       "label": "Calle la X, 29",
     *       "details": "2nd Floor",
     *       "contactPhone": "+34622334455",
     *       "contactPerson": "Sam Romero",
     *       "instructions": "Use the stairs to access this address"
     *     },
     *     {
     *       "type": "DELIVERY",
     *       "lat": 0.2,
     *       "lon": 0.4,
     *       "label": "Calle la Y, 30",
     *       "details": "Blue button of the intercom",
     *       "contactPhone": "+34622334455",
     *       "contactPerson": "Alex Smith",
     *       "instructions": "If recipient is unavailable leave next door"
     *     }
     *   ]
     * }
     *
     * @param orderId
     * @return
     */
    @ResponseBody
    @PostMapping("/{orderId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Cancel a scheduled order. Active orders cannot be canceled.", security = @SecurityRequirement(name = "basicAuth"))
    public Order cancelOrder(@PathVariable String orderId) {
		Order order = OrderMock.createOrderMock();

		order.setId(orderId);
		order.setCode("CANCEL_" + order.getCode() + "_NOW");
		
		Long orderIdLong = Long.valueOf(orderId);
		if (orderIdLong % 2 == 0) {
			order.setState(State.CANCELED);
		} else {
			order.setState(State.ACTIVE);
		}

		return order;
    }


    /**
     * Create an order.
     *
     * If you want the order to be scheduled, provide a scheduleTime in milliseconds (see Order).
     * Otherwise, it'll be scheduled for immediate delivery.
     *
     * Request for an immediate order
     *
     * POST /b2b/orders
     * {
     *   "scheduleTime": null || 1568879245000,
     *   "description": "A 30cm by 30cm box",
     *   "reference": {
     *     "id": "your internal reference"
     *   },
     *   "addresses": [
     *     {
     *       "type": "PICKUP",
     *       "lat": 0.1,
     *       "lon": 0.3,
     *       "label": "Calle la X, 29",
     *       "details": "2nd Floor",
     *       "contactPhone": "+34622334455",
     *       "contactPerson": "Sam Romero",
     *       "instructions": "Use the stairs to access this address"
     *     },
     *     {
     *       "type": "DELIVERY",
     *       "lat": 0.1,
     *       "lon": 0.3,
     *       "label": "Calle la X, 30",
     *       "details": "Blue button of the intercom",
     *       "contactPhone": "+34622334455",
     *       "contactPerson": "Alex Smith",
     *       "instructions": "If recipient is unavailable leave next door"
     *     }
     *   ]
     * }
     *
     * Response
     *
     * {
     *   "id": 123456789,
     *   "code": "AA12AA1BB",
     *   "state": "SCHEDULED",
     *   "scheduleTime": 1568879245000,
     *   "description": "A 30cm by 30cm box",
     *   "reference": {
     *     "id": "your internal reference"
     *   },
     *   "addresses": [
     *     {
     *       "type": "PICKUP",
     *       "lat": 0.1,
     *       "lon": 0.3,
     *       "label": "Calle la X, 29",
     *       "details": "2nd Floor",
     *       "contactPhone": "+34622334455",
     *       "contactPerson": "Sam Romero",
     *       "instructions": "Use the stairs to access this address"
     *     },
     *     {
     *       "type": "DELIVERY",
     *       "lat": 0.1,
     *       "lon": 0.3,
     *       "label": "Calle la X, 30",
     *       "details": "Blue button of the intercom",
     *       "contactPhone": "+34622334455",
     *       "contactPerson": "Alex Smith",
     *       "instructions": "If recipient is unavailable leave next door"
     *     }
     *   ]
     * }
     *
     * @param order
     * @return
     */
    private Order createOneWayOrder(Order order) {
        order.setId("OWO" + RandomUtils.nextInt());
        order.setState(State.SCHEDULED);
        if (order.getScheduleTime() == null) {
            order.setScheduleTime(ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant().toEpochMilli());
            order.setCode("OWO_" + order.getId() + "_NOW");
        } else {
            order.setCode("OWO_" + order.getId() + "_SCHE");
        }

        return order;
    }

    /**
     * Create a return order by adding a second DELIVERY address that is identical to the PICKUP (label, lat, lon must be the same).
     *
     * Request for a RETURN order
     *
     * POST /b2b/orders
     * {
     *   "scheduleTime": null,
     *   "description": "A 30cm by 30cm box",
     *   "reference": {
     *     "id": "your internal reference"
     *   },
     *   "addresses": [
     *     {
     *       "type": "PICKUP",
     *       "lat": 0.1,
     *       "lon": 0.3,
     *       "label": "Calle la X, 29",
     *       "details": "2nd Floor",
     *       "contactPhone": "+34622334455",
     *       "contactPerson": "Sam Romero",
     *       "instructions": "Use the stairs to access this address"
     *     },
     *     {
     *       "type": "DELIVERY",
     *       "lat": 1.2,
     *       "lon": 4.5,
     *       "label": "Calle la Y, 30",
     *       "details": "Blue button of the intercom",
     *       "contactPhone": "+34622334466",
     *       "contactPerson": "Alex Smith",
     *       "instructions": "If recipient is unavailable leave next door"
     *     },
     *     {
     *       "type": "DELIVERY",
     *       "lat": 0.1,
     *       "lon": 0.3,
     *       "label": "Calle la X, 29",
     *       "details": "2nd Floor",
     *       "contactPhone": "+34622334455",
     *       "contactPerson": "Sam Romero",
     *       "instructions": "Use the stairs to access this address"
     *     }
     *   ]
     * }
     *
     * @param order
     */
    private Order createReturnOrder(Order order) {
        order.setId("RO" + RandomUtils.nextInt());
        order.setState(State.SCHEDULED);
        if (order.getScheduleTime() == null) {
            order.setScheduleTime(ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant().toEpochMilli());
            order.setCode("RO_" + order.getId() + "_NOW");
        } else {
            order.setCode("RO_" + order.getId() + "_SCHE");
        }

        return order;
    }

    /**
     * Create a multiple pick-up order by creating up to eight PICKUP addresses and one DELIVERY address.
     *
     * Request for a MULTIPLE PICK-UP order
     *
     * POST /b2b/orders
     * {
     *     "scheduleTime": null,
     *     "description": "A 30cm by 30cm box",
     *     "reference": {
     *       "id": "your internal reference"
     *     },
     *     "addresses": [
     *         {
     *           "type": "PICKUP",
     *           "lat": 0.1,
     *           "lon": 0.3,
     *           "label": "Calle la X, 29",
     *           "details": "2nd Floor",
     *           "contactPhone": "+34622334455",
     *           "contactPerson": "Sam Romero",
     *           "instructions": "Use the stairs to access this address"
     *         },
     *         {
     *           "type": "PICKUP",
     *           "lat": 0.11,
     *           "lon": 0.31,
     *           "label": "Calle la J, 40",
     *           "details": "3rd Floor",
     *           "contactPhone": "+34622334466",
     *           "contactPerson": "Rosa Smith",
     *           "instructions": "If recipient is unavailable leave at reception"
     *         },
     *         {
     *           "type": "PICKUP",
     *           "lat": 0.12,
     *           "lon": 0.32,
     *           "label": "Calle la Y, 56",
     *           "details": "Ground floor",
     *           "contactPhone": "+34622334477",
     *           "contactPerson": "Ana Feliz",
     *           "instructions": "Ask for Ramiro"
     *         },
     *         {
     *           "type": "DELIVERY",
     *           "lat": 0.13,
     *           "lon": 0.33,
     *           "label": "Calle la Z, 30",
     *           "details": "Blue button of the intercom",
     *           "contactPhone": "+34622334488",
     *           "contactPerson": "Alex Rossi",
     *           "instructions": "If recipient is unavailable leave next door"
     *         }
     *     ]
     * }
     *
     * @param order
     */
    private Order createMultiplePickUpOrder(Order order) {
        order.setId("MPUO" + RandomUtils.nextInt());
        order.setState(State.SCHEDULED);
        if (order.getScheduleTime() == null) {
            order.setScheduleTime(ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant().toEpochMilli());
            order.setCode("MPUO_" + order.getId() + "_NOW");
        } else {
            order.setCode("MPUO_" + order.getId() + "_SCHE");
        }

        return order;
    }

    /**
     * Create a multiple drop-off order by adding one PICKUP and up to eight DELIVERY addresses.
     *
     * Request for a MULTIPLE DROP-OFF order
     *
     * POST /b2b/orders
     * {
     *     "scheduleTime": null,
     *     "description": "A 30cm by 30cm box",
     *     "reference": {
     *       "id": "your internal reference"
     *     },
     *     "addresses": [
     *         {
     *           "type": "PICKUP",
     *           "lat": 0.1,
     *           "lon": 0.3,
     *           "label": "Calle la J, 29",
     *           "details": "2nd Floor",
     *           "contactPhone": "+34622334455",
     *           "contactPerson": "Sam Romero",
     *           "instructions": "Use the stairs to access this address"
     *         },
     *         {
     *           "type": "DELIVERY",
     *           "lat": 0.11,
     *           "lon": 0.31,
     *           "label": "Calle la X, 40",
     *           "details": "3rd Floor",
     *           "contactPhone": "+34622334466",
     *           "contactPerson": "Rosa Smith",
     *           "instructions": "If recipient is unavailable leave next door"
     *         },
     *         {
     *           "type": "DELIVERY",
     *           "lat": 0.12,
     *           "lon": 0.32,
     *           "label": "Calle la Y, 56",
     *           "details": "Ground floor",
     *           "contactPhone": "+34622334477",
     *           "contactPerson": "Ana Feliz",
     *           "instructions": "Leave the box near the entrance"
     *         },
     *         {
     *           "type": "DELIVERY",
     *           "lat": 0.14,
     *           "lon": 0.35,
     *           "label": "Calle la Z, 30",
     *           "details": "Blue button of the intercom",
     *           "contactPhone": "+34622334488",
     *           "contactPerson": "Alex Smith",
     *           "instructions": "If recipient is unavailable leave at reception"
     *         }
     *     ]
     * }
     *
     * @param order
     */
    private Order createMultipleDropOffOrder(Order order) {
        order.setId("MDOO" + RandomUtils.nextInt());
        order.setState(State.SCHEDULED);
        if (order.getScheduleTime() == null) {
            order.setScheduleTime(ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant().toEpochMilli());
            order.setCode("MDOO_" + order.getId() + "_NOW");
        } else {
            order.setCode("MDOO_" + order.getId() + "_SCHE");
        }

        return order;
    }

    
    

}


