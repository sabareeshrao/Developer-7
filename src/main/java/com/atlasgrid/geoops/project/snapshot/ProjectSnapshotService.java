package com.atlasgrid.geoops.project.snapshot;

import com.atlasgrid.geoops.project.application.ProjectService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.Objects;

/**
 * Trusted internal Java-serialization boundary for point-in-time catalog
 * snapshots.
 *
 * <p>This is not the public CSV or JSON exchange format. A dedicated snapshot
 * representation keeps the core GeoProject domain model decoupled from Java
 * serialization compatibility.</p>
 */
@Service
public class ProjectSnapshotService {

    private static final long MAX_STREAM_BYTES = 5_000_000L;
    private static final long MAX_REFERENCES = 10_000L;
    private static final long MAX_DEPTH = 8L;

    private final ProjectService projectService;

    public ProjectSnapshotService(ProjectService projectService) {
        this.projectService = projectService;
    }

    public byte[] serializeCurrentCatalog() {
        ProjectSnapshotEntry[] entries = projectService.findAll()
                .stream()
                .map(ProjectSnapshotEntry::from)
                .toArray(ProjectSnapshotEntry[]::new);

        ProjectSnapshotDocument snapshot =
                new ProjectSnapshotDocument(
                        ProjectSnapshotDocument.CURRENT_SCHEMA_VERSION,
                        System.currentTimeMillis(),
                        entries
                );

        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(snapshot);
            output.flush();
            return bytes.toByteArray();
        } catch (IOException exception) {
            throw new ProjectSnapshotException(
                    ProjectSnapshotFailure.SERIALIZATION_FAILED,
                    "Unable to serialize GeoOps project snapshot",
                    exception
            );
        }
    }

    public ProjectSnapshotDocument deserializeSnapshot(byte[] serialized) {
        Objects.requireNonNull(serialized, "serialized");

        try (ObjectInputStream input = new ObjectInputStream(
                new ByteArrayInputStream(serialized)
        )) {
            input.setObjectInputFilter(ProjectSnapshotService::filterSnapshot);

            Object value = input.readObject();

            if (!(value instanceof ProjectSnapshotDocument snapshot)) {
                throw new ProjectSnapshotException(
                        ProjectSnapshotFailure.WRONG_ROOT_TYPE,
                        "Serialized data is not a GeoOps project snapshot"
                );
            }

            if (snapshot.schemaVersion()
                    != ProjectSnapshotDocument.CURRENT_SCHEMA_VERSION) {
                throw new ProjectSnapshotException(
                        ProjectSnapshotFailure.UNSUPPORTED_SCHEMA,
                        "Unsupported GeoOps snapshot schema version: "
                                + snapshot.schemaVersion()
                );
            }

            return snapshot;
        } catch (ProjectSnapshotException exception) {
            throw exception;
        } catch (StreamCorruptedException
                 | EOFException
                 | OptionalDataException exception) {
            throw new ProjectSnapshotException(
                    ProjectSnapshotFailure.CORRUPT_STREAM,
                    "GeoOps project snapshot stream is corrupt or incomplete",
                    exception
            );
        } catch (InvalidClassException exception) {
            ProjectSnapshotFailure failure =
                    isFilterRejection(exception)
                            ? ProjectSnapshotFailure.REJECTED_TYPE
                            : ProjectSnapshotFailure.INCOMPATIBLE_CLASS;

            throw new ProjectSnapshotException(
                    failure,
                    failure == ProjectSnapshotFailure.REJECTED_TYPE
                            ? "GeoOps project snapshot contains a disallowed type"
                            : "GeoOps project snapshot is class-incompatible",
                    exception
            );
        } catch (ClassNotFoundException exception) {
            throw new ProjectSnapshotException(
                    ProjectSnapshotFailure.MISSING_CLASS,
                    "GeoOps project snapshot requires a class that is not available",
                    exception
            );
        } catch (IOException exception) {
            throw new ProjectSnapshotException(
                    ProjectSnapshotFailure.IO_FAILURE,
                    "Unable to read GeoOps project snapshot",
                    exception
            );
        }
    }

    private static boolean isFilterRejection(
            InvalidClassException exception
    ) {
        String message = exception.getMessage();
        return message != null
                && message.contains("filter status: REJECTED");
    }

    private static ObjectInputFilter.Status filterSnapshot(
            ObjectInputFilter.FilterInfo info
    ) {
        if (info.depth() > MAX_DEPTH
                || info.references() > MAX_REFERENCES
                || info.streamBytes() > MAX_STREAM_BYTES) {
            return ObjectInputFilter.Status.REJECTED;
        }

        Class<?> serialClass = info.serialClass();

        if (serialClass == null) {
            return ObjectInputFilter.Status.UNDECIDED;
        }

        if (serialClass == ProjectSnapshotDocument.class
                || serialClass == ProjectSnapshotEntry.class
                || serialClass == String.class) {
            return ObjectInputFilter.Status.ALLOWED;
        }

        if (serialClass.isArray()
                && serialClass.getComponentType()
                == ProjectSnapshotEntry.class) {
            return ObjectInputFilter.Status.ALLOWED;
        }

        return ObjectInputFilter.Status.REJECTED;
    }
}
