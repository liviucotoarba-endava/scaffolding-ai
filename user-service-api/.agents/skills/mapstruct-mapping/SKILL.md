---
name: mapstruct-mapping
description: Add or update MapStruct mappings in user-service-api using the existing mapper configuration and tests.
---

# MapStruct Mapping

- Read the mapper, source and target types, and nearby tests before changing mappings.
- Put mappings on mapper interfaces and use `@Mapper(config = MappingConfiguration.class)`. The shared config uses Spring components and treats unmapped target fields as errors; resolve new target fields explicitly.
- For create mappings, ignore generated identifiers. For update mappings, use `@MappingTarget` and preserve identifiers by ignoring them.
- Test mapped values and preserved or ignored fields with JUnit Jupiter and AssertJ. Use `Mappers.getMapper` in mapper unit tests; don’t mock the mapper being tested.
- Run the focused test with `./mvnw -Dtest=UserMapperTest test`; run `./mvnw test` when changes affect multiple mappings or generated code.
