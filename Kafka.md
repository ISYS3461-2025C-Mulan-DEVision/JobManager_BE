# Job Applicant Kafka Config for Applicant Profile Create And Update


## User Created Event

### Event name
- name: user-registered
### DTO
```
public class UserRegisteredEvent {

    /**
     * User ID - same ID will be used in user-service.
     */
    private UUID userId;

    /**
     * User's email address.
     */
    private String email;

    /**
     * User's first name.
     */
    private String firstName;

    /**
     * User's last name.
     */
    private String lastName;

    /**
     * Country abbreviation (2-letter), mandatory at registration.
     */
    private String countryAbbreviation;

    /**
     * Optional phone number.
     */
    private String phone;

    /**
     * Optional street address (name/number).
     */
    private String address;

    /**
     * Optional city name.
     */
    private String city;

    /**
     * Timestamp when registration occurred.
     */
    @Builder.Default
    private LocalDateTime registeredAt = LocalDateTime.now();
}
```


## User Update Event :
- Send by: user service
- Trigger if there is a significant change made by the user (skill or country)
### Event name
- name: user-profile-updated
### DTO
```
public class UserProfileUpdatedEvent {

    /**
     * Unique ID for this specific event instance.
     */
    @Builder.Default
    private UUID eventId = UUID.randomUUID();

    /**
     * The ID of the user whose profile was updated.
     */
    private UUID userId;

    /**
     * The type of update that occurred.
     */
    private UpdateType updateType;

    /**
     * The user's new country ID. Only present if updateType is COUNTRY.
     */
    private UUID countryId;

    /**
     * The user's complete list of skill IDs. Only present if updateType is SKILLS.
     */
    private List<UUID> skillIds;

    /**
     * Timestamp when the update occurred.
     */
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * Enum to specify what part of the profile was updated.
     */
    public enum UpdateType {
        SKILLS,
        COUNTRY
    }
}
```