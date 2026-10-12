package fr.paris.lutece.plugins.notifygru.modules.extendcomment.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.extend.business.extender.ResourceExtenderDTO;
import fr.paris.lutece.plugins.extend.business.extender.ResourceExtenderDTOFilter;
import fr.paris.lutece.plugins.extend.modules.comment.business.config.CommentExtenderConfig;
import fr.paris.lutece.plugins.extend.modules.comment.service.extender.CommentResourceExtender;
import fr.paris.lutece.plugins.extend.modules.comment.util.constants.CommentConstants;
import fr.paris.lutece.plugins.extend.service.extender.IResourceExtenderService;
import fr.paris.lutece.plugins.extend.service.extender.config.IResourceExtenderConfigService;
import fr.paris.lutece.plugins.workflow.service.provider.ProviderManagerUtil;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.provider.IProvider;
import fr.paris.lutece.plugins.workflowcore.service.provider.IProviderManager;
import fr.paris.lutece.plugins.workflowcore.service.provider.ProviderDescription;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * Provider manager of the comment extenders: one provider per comment extender.
 */
@ApplicationScoped
@Named( ExtendCommentProviderManager.ID )
public class ExtendCommentProviderManager implements IProviderManager
{
    public static final String ID = "notifygru-extendcomment.provider-manager";

    private static final String MESSAGE_PROVIDER_LABEL = "module.notifygru.extendcomment.module.providerextendcomment";

    @Inject
    private IActionService _actionService;

    @Inject
    private IResourceExtenderService _extenderService;

    @Inject
    @Named( CommentConstants.BEAN_CONFIG_SERVICE )
    private IResourceExtenderConfigService _configService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId( )
    {
        return ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IProvider createProvider( String strProviderId, ResourceHistory resourceHistory, HttpServletRequest request )
    {
        return new ExtendCommentProvider( ProviderManagerUtil.buildCompleteProviderId( getId( ), strProviderId ), strProviderId, resourceHistory );
    }

    /**
     * {@inheritDoc}
     * <p>
     * Only the comment extenders linked to the workflow of the task are returned
     * </p>
     */
    @Override
    public Collection<ProviderDescription> getAllProviderDescriptions( ITask task )
    {
        Collection<ProviderDescription> collectionProviderDescriptions = new ArrayList<>( );
        ResourceExtenderDTOFilter filter = new ResourceExtenderDTOFilter( );
        filter.setFilterExtenderType( CommentResourceExtender.EXTENDER_TYPE_COMMENT );
        List<ResourceExtenderDTO> listExtendComment = _extenderService.findByFilter( filter );

        for ( ResourceExtenderDTO resource : listExtendComment )
        {
            Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );
            Workflow workflow = action.getWorkflow( );

            CommentExtenderConfig conf = _configService.find( Integer.parseInt( resource.getResourceId( ) ) );

            if ( conf != null && workflow.getId( ) == conf.getIdWorkflow( ) )
            {
                ProviderDescription providerDescription = getProviderDescription( String.valueOf( resource.getIdExtender( ) ) );

                if ( providerDescription != null )
                {
                    collectionProviderDescriptions.add( providerDescription );
                }
            }
        }
        return collectionProviderDescriptions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProviderDescription getProviderDescription( String strProviderId )
    {
        ResourceExtenderDTO extendComment = _extenderService.findByPrimaryKey( Integer.parseInt( strProviderId ) );
        if ( extendComment == null || !extendComment.isIsActive( ) )
        {
            return null;
        }
        ProviderDescription providerDescription = new ProviderDescription( String.valueOf( extendComment.getIdExtender( ) ),
                I18nService.getLocalizedString( MESSAGE_PROVIDER_LABEL, I18nService.getDefaultLocale( ) ) + extendComment.getIdExtender( ) );
        providerDescription.setMarkerDescriptions( ExtendCommentProvider.getProviderMarkerDescriptions( extendComment.getIdExtender( ) ) );

        return providerDescription;
    }
}
