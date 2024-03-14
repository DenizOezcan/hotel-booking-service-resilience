# hotel-booking-service

## What problem does this service solve?

- getting offers for the available rooms
- booking one of the available hotel rooms

## Which software design patterns are used?

### Proxy pattern

- The Proxy Pattern is seen in `CachingRoomServiceImpl#getRooms` where all the rooms are cached only when it's called as to not waste resources. The implementation is simple and straightforward, however, sometimes, they can cause multiple database calls when data is not stale yet (rather, fresh). In our use case, the hotel rooms are always the same. Caching them (rather than calling the service multiple times) is ideal not to overload (as an example) the database with remote calls. To elaborate a more complex example, caching with Redis can be accomplished by copying the interface (or possibly implementing/extending the interface/class) and adding the actual caching strategies in this new proxy class before returning or accessing the data.

### Chain of responsibility

- The Chain of Responsibility Pattern doesn't just mean to use a super class as a way to handle a procedure when `this` cannot handle the request. It can also be used similarly like a linked list where there is a next sibling class which may or may not handle the request. In the application, this behavior can be seen in the booking flow where the booking information found in `BookingDTO` record is passed between classes that could actually handle the request. In our application, it starts from `BookingController#book` to `BookingService#bookRoom` to `BookingHandler#handleBooking`.
- However, a more sophisticated example can be seen in other frameworks, like `express.js`, and their middleware behavior. The behavior of the middlewares in the application is completed by calling `Router#next`. When the `Router#next` function has finally reached an edge (i.e., a route method), it completes that procedure and finishes the call stack. Each middleware present in that `Router` is processed before reaching the terminal function.

### Strategy pattern

- In the application, the Strategy Pattern is used for the offers of the customers. Each customer has a membership that can be any one of the following types: Regular, Gold, or Platinum, having the greater discounts and perks than the previous, respectively. The interface can be seen in `OfferCalculationStrategy` while its implementing subclasses (the different strategies) can be seen in the files `RegularOfferCalculationStrategy`, `GoldOfferCalculationStrategy`, and `PlatinumOfferCalculationStrategy`. Each type has a price differentiator, which is then mapped to the actual price of the room per night. Furthermore, a more "complex" computation is done for Platinum members. They get a free night when they will stay for 5 days, and another free night if they will stay for 10 days.
- Essentially, the Strategy Pattern is just an elaborate and controlled abstraction. As seen in this example, the code is focused more on the subclasses than having an already implemented procedure in the superclass. By having a controlled superclass `OfferCalculationStrategy`, future subclasses (e.g., `DiamondOfferCalculationStrategy`) can be formed while being easily guided and extended.
