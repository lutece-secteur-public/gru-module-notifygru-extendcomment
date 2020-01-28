package fr.paris.lutece.plugins.notifygru.modules.extendcomment.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.extend.business.extender.ResourceExtenderDTO;
import fr.paris.lutece.plugins.extend.business.extender.ResourceExtenderDTOFilter;
import fr.paris.lutece.plugins.extend.modules.comment.business.config.CommentExtenderConfig;
import fr.paris.lutece.plugins.extend.modules.comment.service.extender.CommentResourceExtender;
import fr.paris.lutece.plugins.extend.modules.comment.util.constants.CommentConstants;
import fr.paris.lutece.plugins.extend.service.extender.IResourceExtenderService;
import fr.paris.lutece.plugins.extend.service.extender.ResourceExtenderService;
import fr.paris.lutece.plugins.extend.service.extender.config.IResourceExtenderConfigService;
import fr.paris.lutece.plugins.workflow.service.provider.ProviderManagerUtil;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.provider.AbstractProviderManager;
import fr.paris.lutece.plugins.workflowcore.service.provider.IProvider;
import fr.paris.lutece.plugins.workflowcore.service.provider.ProviderDescription;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;


public class ExtendCommentProviderManager extends AbstractProviderManager {


	 private static final String MESSAGE_PROVIDER_LABEL = "module.notifygru.extendcomment.module.providerextendcomment";
	 @Inject
     private ActionService _actionService;
	 @Inject
	 @Named( CommentConstants.BEAN_CONFIG_SERVICE )
	 private IResourceExtenderConfigService _configService;
    
	public ExtendCommentProviderManager(String strId) {
		super(strId);
	}

	

	@Override
	public IProvider createProvider(String strProviderId, ResourceHistory resourceHistory, HttpServletRequest request) {
		 return new ExtendCommentProvider( ProviderManagerUtil.buildCompleteProviderId( getId( ), strProviderId ), strProviderId, resourceHistory );
	}

	@Override
	public Collection<ProviderDescription> getAllProviderDescriptions(ITask task) {
		
		Collection<ProviderDescription> collectionProviderDescriptions = new ArrayList<>( );
        IResourceExtenderService extenderService = SpringContextService.getBean( ResourceExtenderService.BEAN_SERVICE );
        ResourceExtenderDTOFilter filter= new ResourceExtenderDTOFilter();
        filter.setFilterExtenderType(CommentResourceExtender.EXTENDER_TYPE_COMMENT);
        List<ResourceExtenderDTO> listExtendComment = extenderService.findByFilter(filter);

        for ( ResourceExtenderDTO resource : listExtendComment )
        {
            Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );
            Workflow workflow = action.getWorkflow( );

            CommentExtenderConfig conf= (CommentExtenderConfig) _configService.find(Integer.parseInt(resource.getResourceId()));
         
            int idWorkflow= conf.getIdWorkflow();
            
            if ( ( workflow.getId( ) == idWorkflow ) )
            {
             
                collectionProviderDescriptions.add( getProviderDescription(String.valueOf(resource.getIdExtender( ))));
            }
        }
        return collectionProviderDescriptions;
	}

	@Override
	public ProviderDescription getProviderDescription(String strProviderId) {
		
		 IResourceExtenderService extenderService = SpringContextService.getBean( ResourceExtenderService.BEAN_SERVICE );
	     ResourceExtenderDTO extendComment = extenderService.findByPrimaryKey(Integer.parseInt(strProviderId));
        if ( !extendComment.isIsActive() )
        {
            return null;
        }
        ProviderDescription providerDescription = new ProviderDescription( String.valueOf( extendComment.getIdExtender( ) ), I18nService.getLocalizedString(
                MESSAGE_PROVIDER_LABEL, I18nService.getDefaultLocale( ) ) + extendComment.getIdExtender());
        providerDescription.setMarkerDescriptions( ExtendCommentProvider.getProviderMarkerDescriptions( extendComment.getIdExtender() ) );

        return providerDescription;
	}

}
