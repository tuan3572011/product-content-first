INSERT INTO oauth_client_details
	(client_id, client_secret, scope, authorized_grant_types,
	web_server_redirect_uri, authorities, access_token_validity,
	refresh_token_validity, additional_information, autoapprove)
VALUES
	('partner-client', 'partner-client', 'login,read,sensitive',
	'password,authorization_code,refresh_token,client_credentials', null, null, 3600, 1, null, true);
