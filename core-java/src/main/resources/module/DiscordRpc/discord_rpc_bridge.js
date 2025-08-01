const RPC = require('discord-rpc');

const clientId = '1394002096562638960';

const rpc = new RPC.Client({ transport: 'ipc' });

rpc.on('ready', () => {
    console.log('Discord RPC ready');

    rpc.setActivity({
        details: 'Browser VWeb 0.0.6-SNAPSHOT',
        state: 'Alpha Testing',
        startTimestamp: Math.floor(Date.now() / 1000),
        largeImageKey: 'gmp',
        largeImageText: 'GMP Protocol',
        smallImageKey: 'vweb',
        smallImageText: 'VWeb OMXium',
        instance: false,
    });
});

rpc.login({ clientId }).catch(console.error);

setInterval(() => {}, 1000);