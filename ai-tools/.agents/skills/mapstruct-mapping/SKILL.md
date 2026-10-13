---
name: mapstruct-mapping
description: Add or update MapStruct mappings using the project's existing mapper configuration, model conventions, and tests.
---

# MapStruct Mapping

- Read the mapper, source and target types, and nearby tests before changing mappings.
- Put mappings on mapper interfaces and use `@Mapper(config = MappingConfiguration.class)`. The shared config uses Spring components and treats unmapped target fields as errors; resolve new target fields explicitly.
- Follow the owning model's rules for identifiers and metadata: ignore database-generated identifiers on create, and explicitly generate application-owned identifiers or timestamps when required. Preserve caller-provided fields that belong in the target contract.
- When updating an existing mutable object, use `@MappingTarget` and ignore identifiers or metadata that must remain unchanged. For immutable targets, follow the existing construction mappings.
- Test mapped values and generated, preserved, or ignored fields with JUnit Jupiter and AssertJ. Use `Mappers.getMapper` in mapper unit tests; don’t mock the mapper being tested. For generated timestamps, assert a bounded interval instead of an exact current time.
- Run the relevant mapper test with `./mvnw -Dtest=ClassNameTest test`, replacing `ClassNameTest` with the actual test class; run `./mvnw test` when changes affect multiple mappings or generated code.
