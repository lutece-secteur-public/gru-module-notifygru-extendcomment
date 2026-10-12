package fr.paris.lutece.plugins.notifygru.modules.extendcomment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.extend.modules.comment.business.Comment;
import fr.paris.lutece.plugins.extend.modules.comment.business.ICommentDAO;
import fr.paris.lutece.plugins.extend.modules.comment.service.CommentPlugin;
import fr.paris.lutece.plugins.workflow.service.provider.ProviderManagerUtil;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.provider.IProvider;
import fr.paris.lutece.plugins.workflowcore.service.provider.IProviderManager;
import fr.paris.lutece.plugins.workflowcore.service.provider.InfoMarker;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

/**
 * The comment provider manager, looked up by its id as the workflow tasks do, and the markers of a comment.
 */
public class ExtendCommentProviderManagerTest extends LuteceTestCase
{
    private static final String RESOURCE_TYPE = "unit_test_notifygru";
    private static final String ID_RESOURCE = "42";

    @Inject
    private ICommentDAO _commentDAO;

    /**
     * Removes the comments of the test resource.
     */
    @AfterEach
    public void removeComments( )
    {
        _commentDAO.deleteByResource( ID_RESOURCE, RESOURCE_TYPE, CommentPlugin.getPlugin( ) );
    }

    /**
     * The workflow finds the manager by the id stored in a task configuration.
     */
    @Test
    public void testManagerIsResolvedByItsId( )
    {
        IProviderManager manager = ProviderManagerUtil.retrieveProviderManager( ExtendCommentProviderManager.ID );

        assertNotNull( manager, "provider manager resolved" );
        assertEquals( ExtendCommentProviderManager.ID, manager.getId( ), "provider manager id" );
    }

    /**
     * The provider created for a comment of the workflow gives the comment values as markers.
     */
    @Test
    public void testProviderGivesTheCommentMarkers( )
    {
        Timestamp date = Timestamp.valueOf( LocalDateTime.of( 2026, 3, 14, 10, 30 ) );
        Comment comment = new Comment( );
        comment.setIdExtendableResource( ID_RESOURCE );
        comment.setExtendableResourceType( RESOURCE_TYPE );
        comment.setName( "Jeanne Test" );
        comment.setEmail( "jeanne.test@e2e.local" );
        comment.setIpAddress( "127.0.0.1" );
        comment.setComment( "Unit test comment" );
        comment.setPublished( true );
        comment.setDateComment( date );
        comment.setDateLastModif( date );
        _commentDAO.insert( comment, CommentPlugin.getPlugin( ) );

        IProvider provider = ProviderManagerUtil.retrieveProviderManager( ExtendCommentProviderManager.ID ).createProvider( "1",
                newResourceHistory( comment.getIdComment( ) ), null );
        Map<String, String> markers = provider.provideMarkerValues( ).stream( )
                .collect( Collectors.toMap( InfoMarker::getMarker, marker -> String.valueOf( marker.getValue( ) ) ) );

        assertEquals( "Unit test comment", markers.get( ExtendCommentConstants.MARK_COMMENT ), "comment marker" );
        assertEquals( "Jeanne Test", markers.get( ExtendCommentConstants.MARK_NAME ), "name marker" );
        assertEquals( "14/03/2026", markers.get( ExtendCommentConstants.MARK_DATE_COMMENT ), "date marker" );
        assertEquals( ID_RESOURCE, markers.get( ExtendCommentConstants.MARK_ID_EXTENDABLE_RESOURCE ), "resource marker" );
        assertEquals( "true", markers.get( ExtendCommentConstants.MARK_IS_PUBLISHED ), "published marker" );
        assertEquals( "jeanne.test@e2e.local", provider.provideCustomerEmail( ), "customer email" );
        assertEquals( String.valueOf( comment.getIdComment( ) ), provider.provideDemandId( ), "demand id" );
        assertEquals( "", provider.provideDemandTypeId( ), "demand type id left empty" );
    }

    /**
     * A resource history pointing to no comment is refused.
     */
    @Test
    public void testProviderRefusesAnUnknownComment( )
    {
        IProviderManager manager = ProviderManagerUtil.retrieveProviderManager( ExtendCommentProviderManager.ID );

        assertThrows( AppException.class, ( ) -> manager.createProvider( "1", newResourceHistory( -1 ), null ), "unknown comment" );
    }

    /**
     * An extender that does not exist is described by nothing, so the task configuration never lists it.
     */
    @Test
    public void testUnknownExtenderHasNoDescription( )
    {
        IProviderManager manager = ProviderManagerUtil.retrieveProviderManager( ExtendCommentProviderManager.ID );

        assertNull( manager.getProviderDescription( "-1" ), "unknown extender" );
    }

    /**
     * Builds a resource history of a comment.
     *
     * @param nIdComment
     *            the comment id
     * @return the resource history
     */
    private static ResourceHistory newResourceHistory( int nIdComment )
    {
        ResourceHistory resourceHistory = new ResourceHistory( );
        resourceHistory.setIdResource( nIdComment );

        return resourceHistory;
    }
}
