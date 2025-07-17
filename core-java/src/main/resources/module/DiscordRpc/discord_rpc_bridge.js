const RPC = require('discord-rpc');

const clientId = '1394002096562638960';

const rpc = new RPC.Client({ transport: 'ipc' });

rpc.on('ready', () => {
    console.log('Discord RPC ready');

    rpc.setActivity({
        details: 'Статичная надпись',
        state: 'Еще одна строка',
        startTimestamp: Math.floor(Date.now() / 1000),
        buttons: [
            { label: 'Кнопка 1', url: 'https://example.com' },
            { label: 'Кнопка 2', url: 'https://example.org' }
        ],
        largeImageKey: 'vweb',
        largeImageText: 'Большое изображение',
        instance: false,
    });
});

rpc.login({ clientId }).catch(console.error);

setInterval(() => {}, 1000);