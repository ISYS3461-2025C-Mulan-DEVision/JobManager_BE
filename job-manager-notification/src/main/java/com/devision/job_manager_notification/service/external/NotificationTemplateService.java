package com.devision.job_manager_notification.service.external;

import com.devision.job_manager_notification.enums.NotificationType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * External service interface for notification template management.
 * Handles template creation, retrieval, and rendering.
 */
public interface NotificationTemplateService {

    /**
     * Creates a new notification template.
     *
     * @param name the template name
     * @param type the notification type
     * @param subject the template subject
     * @param body the template body with placeholders
     * @return the created template ID
     */
    UUID createTemplate(String name, NotificationType type, String subject, String body);

    /**
     * Updates an existing notification template.
     *
     * @param templateId the template ID
     * @param subject the updated subject
     * @param body the updated body
     * @return true if update was successful
     */
    boolean updateTemplate(UUID templateId, String subject, String body);

    /**
     * Deletes a notification template.
     *
     * @param templateId the template ID
     * @return true if deletion was successful
     */
    boolean deleteTemplate(UUID templateId);

    /**
     * Gets a template by ID.
     *
     * @param templateId the template ID
     * @return the template content
     */
    String getTemplate(UUID templateId);

    /**
     * Gets all templates for a specific notification type.
     *
     * @param type the notification type
     * @return list of template IDs
     */
    List<UUID> getTemplatesByType(NotificationType type);

    /**
     * Renders a template with provided variables.
     *
     * @param templateId the template ID
     * @param variables the variables to substitute
     * @return the rendered content
     */
    String renderTemplate(UUID templateId, Map<String, Object> variables);

    /**
     * Validates a template syntax.
     *
     * @param templateContent the template content to validate
     * @return true if template is valid
     */
    boolean validateTemplate(String templateContent);

    /**
     * Gets all available variables for a template type.
     *
     * @param type the notification type
     * @return list of available variable names
     */
    List<String> getAvailableVariables(NotificationType type);

    /**
     * Clones an existing template.
     *
     * @param templateId the template ID to clone
     * @param newName the name for the cloned template
     * @return the new template ID
     */
    UUID cloneTemplate(UUID templateId, String newName);

    /**
     * Activates a template.
     *
     * @param templateId the template ID
     * @return true if activation was successful
     */
    boolean activateTemplate(UUID templateId);

    /**
     * Deactivates a template.
     *
     * @param templateId the template ID
     * @return true if deactivation was successful
     */
    boolean deactivateTemplate(UUID templateId);

    /**
     * Gets the active template for a notification type.
     *
     * @param type the notification type
     * @return the active template ID, or null if none
     */
    UUID getActiveTemplate(NotificationType type);

    /**
     * Previews a template with sample data.
     *
     * @param templateId the template ID
     * @return the rendered preview
     */
    String previewTemplate(UUID templateId);

    /**
     * Exports a template to a file format.
     *
     * @param templateId the template ID
     * @param format the export format (HTML, JSON, etc.)
     * @return the exported content
     */
    String exportTemplate(UUID templateId, String format);

    /**
     * Imports a template from external source.
     *
     * @param content the template content
     * @param format the import format
     * @return the created template ID
     */
    UUID importTemplate(String content, String format);

    /**
     * Searches templates by keyword.
     *
     * @param keyword the search keyword
     * @return list of matching template IDs
     */
    List<UUID> searchTemplates(String keyword);

    /**
     * Gets template usage statistics.
     *
     * @param templateId the template ID
     * @return usage count
     */
    long getTemplateUsageCount(UUID templateId);

    /**
     * Validates template variables against provided data.
     *
     * @param templateId the template ID
     * @param variables the variables to validate
     * @return list of missing or invalid variables
     */
    List<String> validateTemplateVariables(UUID templateId, Map<String, Object> variables);
}
