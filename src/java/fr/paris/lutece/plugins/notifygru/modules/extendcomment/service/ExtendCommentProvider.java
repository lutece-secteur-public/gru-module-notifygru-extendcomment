package fr.paris.lutece.plugins.notifygru.modules.extendcomment.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import fr.paris.lutece.plugins.extend.modules.comment.business.Comment;
import fr.paris.lutece.plugins.extend.modules.comment.business.config.CommentExtenderConfig;
import fr.paris.lutece.plugins.extend.modules.comment.service.CommentService;
import fr.paris.lutece.plugins.extend.modules.comment.service.ICommentService;
import fr.paris.lutece.plugins.extend.modules.comment.service.extender.CommentResourceExtender;
import fr.paris.lutece.plugins.extend.modules.comment.util.constants.CommentConstants;
import fr.paris.lutece.plugins.extend.service.extender.config.IResourceExtenderConfigService;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.provider.IProvider;
import fr.paris.lutece.plugins.workflowcore.service.provider.InfoMarker;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class ExtendCommentProvider implements IProvider {

	// PROPERTY KEY
    private static final String PROPERTY_SMS_SENDER_NAME = AppPropertiesService.getProperty( "notifygru-extendcomment.gruprovider.sms.sendername");
    private static final String PROPERTIE_DATE_FORMAT = AppPropertiesService.getProperty( "notifygru-extendcomment.dateformat", "dd-MM-yyyy" );

    // SERVICES
    private ICommentService _commentService = SpringContextService.getBean( CommentService.BEAN_SERVICE );
	@Inject
	@Named( CommentConstants.BEAN_CONFIG_SERVICE )
	private IResourceExtenderConfigService _configService;
	 
     private final Comment _comment;

	
    public ExtendCommentProvider( String beanProviderName, String strProviderId, ResourceHistory resourceHistory)
    {
    	_comment = _commentService.findByPrimaryKey(resourceHistory.getIdResource( ));
    	
    	  if ( _comment == null )
          {
              throw new AppException( "No COMMENT for resource history Id : " + resourceHistory.getIdResource( ) );
          }
    }
	
	@Override
	public String provideCustomerConnectionId() {
		return _comment.getLuteceUserName();

	}

	@Override
	public String provideCustomerEmail() {
		
		return _comment.getEmail();
	}

	@Override
	public String provideCustomerId() {
		
		return _comment.getLuteceUserName();
	}

	@Override
	public String provideCustomerMobilePhone() {
		return null;
	}

	@Override
	public String provideDemandId() {
		
		return String.valueOf( _comment.getIdComment( ));
	}

	@Override
	public String provideDemandReference() {
		
		return CommentResourceExtender.EXTENDER_TYPE_COMMENT+String.valueOf(_comment.getIdComment());
	}

	@Override
	public String provideDemandSubtypeId() {
		return null;
	}

	@Override
	public String provideDemandTypeId() {
		 CommentExtenderConfig conf= (CommentExtenderConfig) _configService.find(_comment.getIdComment());
		 
		return String.valueOf(conf.getIdExtender( ));
	}

	@Override
	public Collection<InfoMarker> provideMarkerValues() {
		
		
		Collection<InfoMarker> collectionNotifyGruMarkers = new ArrayList<>( );
		SimpleDateFormat formatter = new SimpleDateFormat( PROPERTIE_DATE_FORMAT );
		
		collectionNotifyGruMarkers.add( createMarkerValues( ExtendCommentConstants.MARK_COMMENT, _comment.getComment() ) );
		collectionNotifyGruMarkers.add( createMarkerValues( ExtendCommentConstants.MARK_DATE_COMMENT, formatter.format(_comment.getDateComment( ) )) );
		collectionNotifyGruMarkers.add( createMarkerValues( ExtendCommentConstants.MARK_DATE_LAST_MODIFICATION,formatter.format( _comment.getDateLastModif() ) ));
		collectionNotifyGruMarkers.add( createMarkerValues( ExtendCommentConstants.MARK_EXTENDABLE_RESOURCE_TYPE, _comment.getExtendableResourceType() ) );
		collectionNotifyGruMarkers.add( createMarkerValues( ExtendCommentConstants.MARK_ID_EXTENDABLE_RESOURCE, _comment.getIdExtendableResource( ) ) );
		collectionNotifyGruMarkers.add( createMarkerValues( ExtendCommentConstants.MARK_IS_ADMIN_COMMENT,String.valueOf( _comment.getIsAdminComment() )) );
		collectionNotifyGruMarkers.add( createMarkerValues( ExtendCommentConstants.MARK_IS_IMPORTANT, String.valueOf(_comment.getIsImportant() )) );
		collectionNotifyGruMarkers.add( createMarkerValues( ExtendCommentConstants.MARK_LUTECE_USER_NAME, _comment.getLuteceUserName( ) ) );
		collectionNotifyGruMarkers.add( createMarkerValues( ExtendCommentConstants.MARK_NAME, _comment.getName() ) );
		collectionNotifyGruMarkers.add( createMarkerValues( ExtendCommentConstants.MARK_PINNED, String.valueOf(_comment.isPinned() )) );
		collectionNotifyGruMarkers.add( createMarkerValues( ExtendCommentConstants.MARK_IS_PUBLISHED, String.valueOf(_comment.isPublished() ) ));
		
		return collectionNotifyGruMarkers;
	}

	@Override
	public String provideSmsSender() {
		return PROPERTY_SMS_SENDER_NAME ;
	}
	
	/**
     * static method for retrieving descriptions of available marks for a given comment
     * 
     * @param nComentId
     *            id of the nCommentExtender
     * @return Collection of InfoMarker
     */
    public static Collection<InfoMarker> getProviderMarkerDescriptions( int nCommentExtender )
    {
    	Collection<InfoMarker> collectionNotifyGruMarkers = new ArrayList<>( );
		
		collectionNotifyGruMarkers.add( createMarkerDescriptions( ExtendCommentConstants.MARK_COMMENT,ExtendCommentConstants.MESSAGE_MARK_COMMENT ,null ) );
		collectionNotifyGruMarkers.add( createMarkerDescriptions( ExtendCommentConstants.MARK_DATE_COMMENT,ExtendCommentConstants.MESSAGE_MARK_DATE_COMMENT ,null ) );
		collectionNotifyGruMarkers.add( createMarkerDescriptions( ExtendCommentConstants.MARK_DATE_LAST_MODIFICATION,ExtendCommentConstants.MESSAGE_MARK_DATE_LAST_MODIFICATION,null  ));
		collectionNotifyGruMarkers.add( createMarkerDescriptions( ExtendCommentConstants.MARK_EXTENDABLE_RESOURCE_TYPE,ExtendCommentConstants.MESSAGE_MARK_EXTENDABLE_RESOURCE_TYPE ,null ) );
		collectionNotifyGruMarkers.add( createMarkerDescriptions( ExtendCommentConstants.MARK_ID_EXTENDABLE_RESOURCE,ExtendCommentConstants.MESSAGE_MARK_ID_EXTENDABLE_RESOURCE ,null ) );
		collectionNotifyGruMarkers.add( createMarkerDescriptions( ExtendCommentConstants.MARK_IS_ADMIN_COMMENT,ExtendCommentConstants.MESSAGE_MARK_IS_ADMIN_COMMENT,null ) );
		collectionNotifyGruMarkers.add( createMarkerDescriptions( ExtendCommentConstants.MARK_IS_IMPORTANT, ExtendCommentConstants.MESSAGE_MARK_IS_IMPORTANT,null ) );
		collectionNotifyGruMarkers.add( createMarkerDescriptions( ExtendCommentConstants.MARK_LUTECE_USER_NAME,ExtendCommentConstants.MESSAGE_MARK_LUTECE_USER_NAME ,null ) );
		collectionNotifyGruMarkers.add( createMarkerDescriptions( ExtendCommentConstants.MARK_NAME, ExtendCommentConstants.MESSAGE_MARK_NAME,null  ) );
		collectionNotifyGruMarkers.add( createMarkerDescriptions( ExtendCommentConstants.MARK_PINNED, ExtendCommentConstants.MESSAGE_MARK_PINNED,null ) );
		collectionNotifyGruMarkers.add( createMarkerDescriptions( ExtendCommentConstants.MARK_IS_PUBLISHED,ExtendCommentConstants.MESSAGE_MARK_IS_PUBLISHED ,null  ));
		
		return collectionNotifyGruMarkers;
    }


    /**
     * Construct a InfoMarker with value for given parameters
     * 
     * @param strMarker
     * @param strValue
     * @return a InfoMarker
     */
    private static InfoMarker createMarkerValues( String strMarker, String strValue )
    {
        InfoMarker notifyMarker = new InfoMarker( strMarker );
        notifyMarker.setValue( strValue );

        return notifyMarker;
    }
    /**
    * Construct a InfoMarker with descrition for given parameters
    * 
    * @param strMarker
    * @param strDescription
    * @return a InfoMarker
    */
   private static InfoMarker createMarkerDescriptions( String strMarker, String strDescriptionI18n, String strDescription )
   {
       InfoMarker notifyMarker = new InfoMarker( strMarker );
       if ( strDescriptionI18n != null )
       {
           notifyMarker.setDescription( I18nService.getLocalizedString( strDescriptionI18n, I18nService.getDefaultLocale( ) ) );
       }
       else
       {
           notifyMarker.setDescription( strDescription );
       }

       return notifyMarker;
}
}
